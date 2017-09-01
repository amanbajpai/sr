// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login.location.failed;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CheckLocationFailedActivity_ViewBinding implements Unbinder {
  private CheckLocationFailedActivity target;

  private View view2131558560;

  private View view2131558553;

  @UiThread
  public CheckLocationFailedActivity_ViewBinding(CheckLocationFailedActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CheckLocationFailedActivity_ViewBinding(final CheckLocationFailedActivity target,
      View source) {
    this.target = target;

    View view;
    target.emailEditText = Utils.findRequiredViewAsType(source, R.id.emailEditText, "field 'emailEditText'", CustomEditTextView.class);
    target.countryEditText = Utils.findRequiredViewAsType(source, R.id.countryEditText, "field 'countryEditText'", CustomEditTextView.class);
    target.cityEditText = Utils.findRequiredViewAsType(source, R.id.cityEditText, "field 'cityEditText'", CustomEditTextView.class);
    view = Utils.findRequiredView(source, R.id.subscribeButton, "method 'onViewClicked'");
    view2131558560 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.cancelButton, "method 'onViewClicked'");
    view2131558553 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    CheckLocationFailedActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.emailEditText = null;
    target.countryEditText = null;
    target.cityEditText = null;

    view2131558560.setOnClickListener(null);
    view2131558560 = null;
    view2131558553.setOnClickListener(null);
    view2131558553 = null;
  }
}
