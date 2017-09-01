// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login.terms;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.webkit.WebView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomCheckBox;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TermsAndConditionActivity_ViewBinding implements Unbinder {
  private TermsAndConditionActivity target;

  private View view2131558554;

  @UiThread
  public TermsAndConditionActivity_ViewBinding(TermsAndConditionActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public TermsAndConditionActivity_ViewBinding(final TermsAndConditionActivity target,
      View source) {
    this.target = target;

    View view;
    target.webView = Utils.findRequiredViewAsType(source, R.id.webView, "field 'webView'", WebView.class);
    target.acceptTC = Utils.findRequiredViewAsType(source, R.id.acceptTC, "field 'acceptTC'", CustomCheckBox.class);
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
    TermsAndConditionActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.webView = null;
    target.acceptTC = null;
    target.continueButton = null;

    view2131558554.setOnClickListener(null);
    view2131558554 = null;
  }
}
