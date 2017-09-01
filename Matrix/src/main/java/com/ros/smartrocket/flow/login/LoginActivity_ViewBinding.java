// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.ui.views.SocialLoginView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoginActivity_ViewBinding implements Unbinder {
  private LoginActivity target;

  private View view2131558566;

  private View view2131558581;

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public LoginActivity_ViewBinding(final LoginActivity target, View source) {
    this.target = target;

    View view;
    target.emailEditText = Utils.findRequiredViewAsType(source, R.id.emailEditText, "field 'emailEditText'", CustomEditTextView.class);
    view = Utils.findRequiredView(source, R.id.continue_btn, "field 'continueWithEmailBtn' and method 'onClick'");
    target.continueWithEmailBtn = Utils.castView(view, R.id.continue_btn, "field 'continueWithEmailBtn'", CustomButton.class);
    view2131558566 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.currentVersion = Utils.findRequiredViewAsType(source, R.id.currentVersion, "field 'currentVersion'", CustomTextView.class);
    target.socialLoginView = Utils.findRequiredViewAsType(source, R.id.social_login_view, "field 'socialLoginView'", SocialLoginView.class);
    view = Utils.findRequiredView(source, R.id.language, "field 'language' and method 'onClick'");
    target.language = Utils.castView(view, R.id.language, "field 'language'", CustomTextView.class);
    view2131558581 = view;
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
    LoginActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.emailEditText = null;
    target.continueWithEmailBtn = null;
    target.currentVersion = null;
    target.socialLoginView = null;
    target.language = null;

    view2131558566.setOnClickListener(null);
    view2131558566 = null;
    view2131558581.setOnClickListener(null);
    view2131558581 = null;
  }
}
