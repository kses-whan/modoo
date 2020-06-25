package com.icure.kses.modoo.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.icure.kses.modoo.R;
import com.icure.kses.modoo.constant.ModooApiCodes;
import com.icure.kses.modoo.constant.ModooConstants;
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

    private FirebaseAnalytics mFirebaseAnalytics;

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

        initAnalytics();
        initLayout();
        transAnimate();
        handleIntent();
        handleHttpData();
        loadImage();
    }

    private void initAnalytics(){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void initLayout(){
        mImageView = (ImageView)findViewById(R.id.image1);
        mTextViewName = (TextView) findViewById(R.id.tv_detail_name);
        mTextViewPrice = (TextView) findViewById(R.id.tv_detail_price);
        textViewAddToCart = (TextView)findViewById(R.id.text_action_bottom1);
        textViewBuyNow = (TextView)findViewById(R.id.text_action_bottom2);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void transAnimate(){
        ViewCompat.setTransitionName(mImageView, VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(mTextViewName, VIEW_NAME_HEADER_NAME);
        ViewCompat.setTransitionName(mTextViewPrice, VIEW_NAME_HEADER_PRICE);
        addTransitionListener();
    }

    private void handleIntent(){
        Intent intent = getIntent();
        if (intent != null) {
            stringImageUri = intent.getStringExtra(ImageListFragment.STRING_IMAGE_URI);
            stringItemCode = intent.getStringExtra(ImageListFragment.STRING_ITEM_CODE);
        }
    }

    private void handleHttpData(){
        moDooViewModel = ViewModelProviders.of(ItemDetailsActivity.this).get(ModooViewModel.class);
        moDooViewModel.getItemDetailData(stringItemCode).observe(ItemDetailsActivity.this, new Observer<ModooItemWrapper>() {
            @Override
            public void onChanged(ModooItemWrapper modooItemWrapper) {
                if(modooItemWrapper != null){
                    if(!modooItemWrapper.resultCode.equalsIgnoreCase(ModooApiCodes.API_RETURNCODE_SUCCESS)){
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

                //analytics event
                Bundle bundle = new Bundle();
                bundle.putString("btn_name", ModooConstants.EVENT_ID_ITEM_DETAILS_ACTIVITY_1);
                bundle.putString("btn_desc", ModooConstants.EVENT_NAME_CART_BTN_ITEM_DETAILS_ACTIVITY);
                mFirebaseAnalytics.logEvent("btn_click", bundle);
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

    private void loadImage() {
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                            loadImage();
                        }
                    }, 500);

                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {}

                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {}

                @Override
                public void onTransitionResume(Transition transition) {}
            });
            return true;
        }
        return false;
    }
}
