// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login.external;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomTextView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ExternalAuthDetailsActivity_ViewBinding implements Unbinder {
  private ExternalAuthDetailsActivity target;

  private View view2131558562;

  private View view2131558563;

  private View view2131558565;

  private View view2131558566;

  @UiThread
  public ExternalAuthDetailsActivity_ViewBinding(ExternalAuthDetailsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ExternalAuthDetailsActivity_ViewBinding(final ExternalAuthDetailsActivity target,
      View source) {
    this.target = target;

    View view;
    target.emailEditText = Utils.findRequiredViewAsType(source, R.id.emailEditText, "field 'emailEditText'", CustomEditTextView.class);
    view = Utils.findRequiredView(source, R.id.birthdayEditText, "field 'birthdayEditText' and method 'onClick'");
    target.birthdayEditText = Utils.castView(view, R.id.birthdayEditText, "field 'birthdayEditText'", CustomEditTextView.class);
    view2131558562 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.txt_why_dob, "field 'txtWhyDob' and method 'onClick'");
    target.txtWhyDob = Utils.castView(view, R.id.txt_why_dob, "field 'txtWhyDob'", CustomTextView.class);
    view2131558563 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.birthLayout = Utils.findRequiredViewAsType(source, R.id.birthLayout, "field 'birthLayout'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.txt_why_email, "field 'txtWhyEmail' and method 'onClick'");
    target.txtWhyEmail = Utils.castView(view, R.id.txt_why_email, "field 'txtWhyEmail'", CustomTextView.class);
    view2131558565 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.emailLayout = Utils.findRequiredViewAsType(source, R.id.emailLayout, "field 'emailLayout'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.continue_btn, "method 'onClick'");
    view2131558566 = view;
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
    ExternalAuthDetailsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.emailEditText = null;
    target.birthdayEditText = null;
    target.txtWhyDob = null;
    target.birthLayout = null;
    target.txtWhyEmail = null;
    target.emailLayout = null;

    view2131558562.setOnClickListener(null);
    view2131558562 = null;
    view2131558563.setOnClickListener(null);
    view2131558563 = null;
    view2131558565.setOnClickListener(null);
    view2131558565 = null;
    view2131558566.setOnClickListener(null);
    view2131558566 = null;
  }
}
