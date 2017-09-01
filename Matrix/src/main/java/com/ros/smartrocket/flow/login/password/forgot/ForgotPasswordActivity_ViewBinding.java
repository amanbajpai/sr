// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login.password.forgot;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ForgotPasswordActivity_ViewBinding implements Unbinder {
  private ForgotPasswordActivity target;

  private View view2131558568;

  private View view2131558553;

  @UiThread
  public ForgotPasswordActivity_ViewBinding(ForgotPasswordActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ForgotPasswordActivity_ViewBinding(final ForgotPasswordActivity target, View source) {
    this.target = target;

    View view;
    target.mailImageView = Utils.findRequiredViewAsType(source, R.id.mailImageView, "field 'mailImageView'", ImageView.class);
    target.emailEditText = Utils.findRequiredViewAsType(source, R.id.emailEditText, "field 'emailEditText'", CustomEditTextView.class);
    view = Utils.findRequiredView(source, R.id.sendButton, "method 'onViewClicked'");
    view2131558568 = view;
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
    ForgotPasswordActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mailImageView = null;
    target.emailEditText = null;

    view2131558568.setOnClickListener(null);
    view2131558568 = null;
    view2131558553.setOnClickListener(null);
    view2131558553 = null;
  }
}
