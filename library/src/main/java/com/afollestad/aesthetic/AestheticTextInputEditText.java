package com.afollestad.aesthetic;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

/** @author Aidan Follestad (afollestad) */
final class AestheticTextInputEditText extends TextInputEditText {

  private CompositeSubscription subs;
  private int backgroundResId;
  private ColorIsDarkState lastState;

  public AestheticTextInputEditText(Context context) {
    super(context);
  }

  public AestheticTextInputEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public AestheticTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs != null) {
      backgroundResId = resolveResId(context, attrs, android.R.attr.background);
    }
  }

  private void invalidateColors(ColorIsDarkState state) {
    this.lastState = state;
    TintHelper.setTintAuto(this, state.color(), true, state.isDark());
    TintHelper.setCursorTint(this, state.color());
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subs = new CompositeSubscription();
    subs.add(
        Aesthetic.get()
            .textColorPrimary()
            .compose(Rx.<Integer>distinctToMainThread())
            .subscribe(ViewTextColorAction.create(this), onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .textColorSecondary()
            .compose(Rx.<Integer>distinctToMainThread())
            .subscribe(ViewHintTextColorAction.create(this), onErrorLogAndRethrow()));
    subs.add(
        Observable.combineLatest(
                ViewUtil.getObservableForResId(
                    getContext(), backgroundResId, Aesthetic.get().colorAccent()),
                Aesthetic.get().isDark(),
                ColorIsDarkState.creator())
            .compose(Rx.<ColorIsDarkState>distinctToMainThread())
            .subscribe(
                new Action1<ColorIsDarkState>() {
                  @Override
                  public void call(ColorIsDarkState colorIsDarkState) {
                    invalidateColors(colorIsDarkState);
                  }
                },
                onErrorLogAndRethrow()));
  }

  @Override
  protected void onDetachedFromWindow() {
    subs.unsubscribe();
    super.onDetachedFromWindow();
  }

  @Override
  public void refreshDrawableState() {
    super.refreshDrawableState();
    if (lastState != null) {
      post(
          new Runnable() {
            @Override
            public void run() {
              invalidateColors(lastState);
            }
          });
    }
  }
}
