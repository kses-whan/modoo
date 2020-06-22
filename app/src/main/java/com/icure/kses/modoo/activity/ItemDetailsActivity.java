package com.icure.kses.modoo.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.icure.kses.modoo.R;
import com.icure.kses.modoo.constant.Modoo_Api_Codes;
import com.icure.kses.modoo.fragments.ImageListFragment;
import com.icure.kses.modoo.fragments.ViewPagerActivity;
import com.icure.kses.modoo.model.ModooItemWrapper;
import com.icure.kses.modoo.model.ModooViewModel;
import com.icure.kses.modoo.notification.NotificationCountSetClass;
import com.icure.kses.modoo.options.CartListActivity;
import com.icure.kses.modoo.utility.ModooDataUtils;
import com.icure.kses.modoo.vo.ModooItemDetail;
import com.icure.kses.modoo.vo.ModooItemList;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;

public class ItemDetailsActivity extends AppCompatActivity {

    String stringImageUri;
    String stringItemCode;

    ImageView mImageView;
    TextView mTextViewName;
    TextView mTextViewPrice;
    TextView textViewAddToCart;
    TextView textViewBuyNow;

    private ModooViewModel moDooViewModel;

    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";
    public static final String VIEW_NAME_HEADER_NAME = "detail:header:name";
    public static final String VIEW_NAME_HEADER_PRICE = "detail:header:price";

    public static final String DETAIL_IMAGE_LIST = "DETAIL_IMAGE_LIST";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        mImageView = (ImageView)findViewById(R.id.image1);
        mTextViewName = (TextView) findViewById(R.id.tv_detail_name);
        mTextViewPrice = (TextView) findViewById(R.id.tv_detail_price);
        textViewAddToCart = (TextView)findViewById(R.id.text_action_bottom1);
        textViewBuyNow = (TextView)findViewById(R.id.text_action_bottom2);

        ViewCompat.setTransitionName(mImageView, VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(mTextViewName, VIEW_NAME_HEADER_NAME);
        ViewCompat.setTransitionName(mTextViewPrice, VIEW_NAME_HEADER_PRICE);

        if (getIntent() != null) {
            stringImageUri = getIntent().getStringExtra(ImageListFragment.STRING_IMAGE_URI);
            stringItemCode = getIntent().getStringExtra(ImageListFragment.STRING_ITEM_CODE);
        }

//        Uri uri = Uri.parse(stringImageUri);
//        mImageView.setImageURI(uri);

        Log.i("tagg","getIntent stringItemCode : " + stringItemCode);

        moDooViewModel = ViewModelProviders.of(ItemDetailsActivity.this).get(ModooViewModel.class);
        moDooViewModel.getItemDetailData(stringItemCode).observe(ItemDetailsActivity.this, new Observer<ModooItemWrapper>() {
            @Override
            public void onChanged(ModooItemWrapper modooItemWrapper) {
                if(modooItemWrapper != null){
                    if(!modooItemWrapper.resultCode.equalsIgnoreCase(Modoo_Api_Codes.API_RETURNCODE_SUCCESS)){
                        Toast.makeText(ItemDetailsActivity.this, "getItemDetailData Error 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setDetail(modooItemWrapper.itemDetail);
                } else {
                    Toast.makeText(ItemDetailsActivity.this, "getItemDetailData Error 2", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        loadThumbImage();
        addTransitionListener();
    }

    private void setDetail(final ModooItemDetail modooItemDetail){
        if(modooItemDetail == null){
            Log.i("tagg","ModooItemDetail is null");
            return;
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPagerActivity.class);
                intent.putStringArrayListExtra(DETAIL_IMAGE_LIST, new ArrayList<String>(modooItemDetail.detailImageUrls));
                startActivity(intent);

                overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
            }
        });

        textViewAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModooDataUtils modooDataUtils = new ModooDataUtils();
                modooDataUtils.addCartList(createModooItemList(modooItemDetail));
                Toast.makeText(ItemDetailsActivity.this,"Item added to cart.",Toast.LENGTH_SHORT).show();
                ModooMainActivity.notificationCountCart++;
                NotificationCountSetClass.setNotifyCount(ModooMainActivity.notificationCountCart);
            }
        });

        textViewBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModooDataUtils modooDataUtils = new ModooDataUtils();
                modooDataUtils.addCartList(createModooItemList(modooItemDetail));
                ModooMainActivity.notificationCountCart++;
                NotificationCountSetClass.setNotifyCount(ModooMainActivity.notificationCountCart);
                startActivity(new Intent(ItemDetailsActivity.this, CartListActivity.class));
            }
        });
    }

    private ModooItemList createModooItemList(ModooItemDetail modooItemDetail){
        ModooItemList mil = new ModooItemList();
        mil.itemCode = modooItemDetail.itemCode;
        mil.thumbUrl = modooItemDetail.thumbUrl;
        mil.repImageUrl = modooItemDetail.repImageUrl;
        mil.itemName = modooItemDetail.itemName;
        mil.itemPrice = modooItemDetail.itemPrice;
        mil.itemCreateDate = modooItemDetail.itemCreateDate;
        return mil;
    }

    /**
     * Load the item's full-size image into our {@link ImageView}.
     */
    private void loadFullSizeImage() {
//        Picasso.with(mImageView.getContext())
//                .load(stringImageUri)
//                .noFade()
//                .noPlaceholder()
//                .into(mImageView);
        String extension = FilenameUtils.getExtension(stringImageUri);
        if(extension.equalsIgnoreCase("gif")){
            Glide.with(mImageView.getContext())
                    .load(stringImageUri)
                    .into(mImageView);
        } else {
            Picasso.with(mImageView.getContext())
                    .load(stringImageUri)
                    .noPlaceholder()
                    .noFade()
                    .into(mImageView);
        }
    }

    private void loadThumbImage() {
//        Picasso.with(mImageView.getContext())
//                .load(stringImageUri)
//                .noFade()
//                .noPlaceholder()
//                .into(mImageView);

        String extension = FilenameUtils.getExtension(stringImageUri);
        if(extension.equalsIgnoreCase("gif")){
            Glide.with(mImageView.getContext())
                    .load(stringImageUri)
                    .into(mImageView);
        } else {
            Picasso.with(mImageView.getContext())
                    .load(stringImageUri)
                    .noPlaceholder()
                    .noFade()
                    .into(mImageView);
        }
    }

    @RequiresApi(21)
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadFullSizeImage();
                        }
                    }, 500);

                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }
}
