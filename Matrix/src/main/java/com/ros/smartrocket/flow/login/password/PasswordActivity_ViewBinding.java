// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login.password;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomCheckBox;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PasswordActivity_ViewBinding implements Unbinder {
  private PasswordActivity target;

  private View view2131558590;

  private View view2131558591;

  @UiThread
  public PasswordActivity_ViewBinding(PasswordActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public PasswordActivity_ViewBinding(final PasswordActivity target, View source) {
    this.target = target;

    View view;
    target.passwordEditText = Utils.findRequiredViewAsType(source, R.id.passwordEditText, "field 'passwordEditText'", CustomEditTextView.class);
    target.rememberMeCheckBox = Utils.findRequiredViewAsType(source, R.id.rememberMeCheckBox, "field 'rememberMeCheckBox'", CustomCheckBox.class);
    view = Utils.findRequiredView(source, R.id.login_btn, "field 'loginButton' and method 'onClick'");
    target.loginButton = Utils.castView(view, R.id.login_btn, "field 'loginButton'", CustomButton.class);
    view2131558590 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.forgotPasswordButton, "method 'onClick'");
    view2131558591 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    PasswordActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.passwordEditText = null;
    target.rememberMeCheckBox = null;
    target.loginButton = null;

    view2131558590.setOnClickListener(null);
    view2131558590 = null;
    view2131558591.setOnClickListener(null);
    view2131558591 = null;
  }
}
