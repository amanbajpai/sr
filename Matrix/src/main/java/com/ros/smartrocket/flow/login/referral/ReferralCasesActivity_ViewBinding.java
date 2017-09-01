// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login.referral;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Spinner;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.views.CustomButton;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReferralCasesActivity_ViewBinding implements Unbinder {
  private ReferralCasesActivity target;

  private View view2131558554;

  @UiThread
  public ReferralCasesActivity_ViewBinding(ReferralCasesActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ReferralCasesActivity_ViewBinding(final ReferralCasesActivity target, View source) {
    this.target = target;

    View view;
    target.referralCasesSpinner = Utils.findRequiredViewAsType(source, R.id.referralCasesSpinner, "field 'referralCasesSpinner'", Spinner.class);
    view = Utils.findRequiredView(source, R.id.continueButton, "field 'continueButton' and method 'onClick'");
    target.continueButton = Utils.castView(view, R.id.continueButton, "field 'continueButton'", CustomButton.class);
    view2131558554 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    ReferralCasesActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.referralCasesSpinner = null;
    target.continueButton = null;

    view2131558554.setOnClickListener(null);
    view2131558554 = null;
  }
}
