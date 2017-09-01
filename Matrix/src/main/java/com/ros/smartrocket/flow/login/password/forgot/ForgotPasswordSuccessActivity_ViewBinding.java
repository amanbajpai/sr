// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login.password.forgot;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.views.CustomTextView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ForgotPasswordSuccessActivity_ViewBinding implements Unbinder {
  private ForgotPasswordSuccessActivity target;

  private View view2131558555;

  @UiThread
  public ForgotPasswordSuccessActivity_ViewBinding(ForgotPasswordSuccessActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ForgotPasswordSuccessActivity_ViewBinding(final ForgotPasswordSuccessActivity target,
      View source) {
    this.target = target;

    View view;
    target.email = Utils.findRequiredViewAsType(source, R.id.email, "field 'email'", CustomTextView.class);
    view = Utils.findRequiredView(source, R.id.okButton, "method 'onViewClicked'");
    view2131558555 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    ForgotPasswordSuccessActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.email = null;

    view2131558555.setOnClickListener(null);
    view2131558555 = null;
  }
}
