package com.afollestad.aesthetic;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.widget.TextView;

import rx.functions.Action1;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
class ViewHintTextColorAction implements Action1<Integer> {

  private final TextView view;

  private ViewHintTextColorAction(TextView view) {
    this.view = view;
  }

  public static ViewHintTextColorAction create(@NonNull TextView view) {
    return new ViewHintTextColorAction(view);
  }

  @Override
  public void call(Integer color) {
    if (view != null) {
      view.setHintTextColor(color);
    }
  }
}
