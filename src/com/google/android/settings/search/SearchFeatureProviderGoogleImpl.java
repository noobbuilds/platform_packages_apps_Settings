package com.google.android.settings.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.settings.search.SearchFeatureProviderImpl;
import com.android.settings.search.SearchFragment;
import com.android.settings.search.SearchResult;

import java.util.List;

public class SearchFeatureProviderGoogleImpl extends SearchFeatureProviderImpl {
  private final String[] RECEIVER_ADDRESS;
  private final String SUBJECT_PREFIX;
  @VisibleForTesting Button mFeedbackButton;
  @VisibleForTesting RelativeLayout mFeedbackPopup;
  @VisibleForTesting boolean mShouldShowFeedbackButton;
  private ImageView mDismissButton;

  public SearchFeatureProviderGoogleImpl() {
    this.RECEIVER_ADDRESS = new String[] {"settings-search-feedback@google.com"};
    this.SUBJECT_PREFIX = "Feedback for: ";
  }

  @VisibleForTesting
  String buildEmailBody(final List<SearchResult> list, final String s, final String s2) {
    final StringBuilder sb = new StringBuilder();
    sb.append("---------------------------------\n");
    sb.append("Additional Feedback\n");
    sb.append("---------------------------------\n");
    sb.append(
        "# YOUR COMMENTS HERE\n# Was a result missing?\n# Can ranking be improved?\n\n\n\n\n\n");
    sb.append("---------------------------------\n");
    sb.append("Query: ");
    sb.append(s);
    sb.append("\n");
    sb.append("---------------------------------\n");
    sb.append("Search Results:\n");
    if (list != null) {
      for (int i = 0; i < list.size(); ++i) {
        final SearchResult searchResult = list.get(i);
        sb.append(Integer.toString(i + 1));
        sb.append(") ");
        sb.append(searchResult.title);
        if (searchResult.summary != null && searchResult.summary.length() > 0) {
          sb.append("\n    ");
          sb.append(searchResult.summary);
        }
        if (searchResult.breadcrumbs != null && searchResult.breadcrumbs.size() > 0) {
          sb.append("\n    ");
          sb.append(searchResult.breadcrumbs);
        }
        sb.append("\n    ");
        sb.append(searchResult.viewType);
        sb.append("\n\n");
      }
    }
    sb.append("---------------------------------\n");
    sb.append("Build Number: ");
    sb.append(s2);
    return sb.toString();
  }

  @Override
  public void hideFeedbackButton() {
    if (this.mFeedbackPopup != null) {
      this.mFeedbackPopup.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void initFeedbackButton() {
    this.mShouldShowFeedbackButton = true;
  }

  @VisibleForTesting
  boolean isDogfood(final Context context) {
    try {
      return false;
    } catch (Exception ex) {
      Log.w("SearchFeature", "Error reading dogfood feedback enabled state", ex);
      return false;
    }
  }

  @VisibleForTesting
  void sendEmailFeedback(final SearchFragment searchFragment) {
    final Activity activity = searchFragment.getActivity();
    final String query = searchFragment.getQuery();
    final Intent putExtra =
        new Intent("android.intent.action.SENDTO")
            .setData(Uri.parse("mailto:"))
            .putExtra("android.intent.extra.EMAIL", this.RECEIVER_ADDRESS)
            .putExtra("android.intent.extra.SUBJECT", "Feedback for: " + query)
            .putExtra(
                "android.intent.extra.TEXT",
                this.buildEmailBody(
                    searchFragment.getSearchResults(), query, Build.VERSION.INCREMENTAL));
    if (activity.getPackageManager().resolveActivity(putExtra, PackageManager.GET_META_DATA)
        != null) {
      searchFragment.startActivity(putExtra);
    }
  }
}

