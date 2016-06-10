package com.intencity.intencity.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.notification.AwardDialogContent;

/**
 * A class to run the notification functionality so we can use it in multiple places.
 *
 * Created by Nick Piscopio on 6/9/16.
 */
public class NotificationUtil
{
   public NotificationUtil(AwardDialogContent award, ImageView awardImage, LinearLayout badgeAmountLayout, TextView title, TextView description, TextView amount)
   {
      int awardImgRes = award.getImgRes();
      if (awardImgRes > 0)
      {
         awardImage.setImageResource(awardImgRes);
         awardImage.setVisibility(View.VISIBLE);

         title.setVisibility(View.GONE);
      }
      else
      {
         title.setText(award.getTitle());
         title.setVisibility(View.VISIBLE);

         awardImage.setVisibility(View.GONE);
      }

      int awardTotal = award.getAmount();

      if (awardTotal > 1)
      {
         badgeAmountLayout.setVisibility(View.VISIBLE);
         amount.setText("x" + awardTotal);
      }
      else
      {
         badgeAmountLayout.setVisibility(View.GONE);
      }

      description.setText(award.getDescription());
   }
}