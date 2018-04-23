/*
 * Copyright (c) 2016. Ted Park. All Rights Reserved
 */

package com.gun0912.tedpicker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.gun0912.tedpicker.custom.adapter.SpacesItemDecoration;
import com.gun0912.tedpicker.util.Util;
import com.simons.owner.traffickcam2.MainActivity;
import com.simons.owner.traffickcam2.R;

import java.util.ArrayList;


public class ImagePickerActivity extends AppCompatActivity implements CameraHostProvider {

    /**
     * Returns the parcelled image uris in the intent with this extra.
     */
    public static final String EXTRA_IMAGE_URIS = "image_uris";
    public static CwacCameraFragment.MyCameraHost mMyCameraHost;
    // initialize with default config.
    private static Config mConfig = new Config();
    /**
     * Key to persist the list when saving the state of the activity.
     */

    public ArrayList<Uri> mSelectedImages;
    protected Toolbar toolbar;
    View view_root;
    TextView mSelectedImageEmptyMessage;
    View view_selected_photos_container;
    RecyclerView rc_selected_photos;
    TextView tv_selected_title;
    ViewPager mViewPager;
    TabLayout tabLayout;
    PagerAdapter_Picker adapter;
    Adapter_SelectedPhoto adapter_selectedPhoto;
    AlertDialog dialog;
    CheckBox checkBoxes[];
    ImageView dialogView;
    ProgressBar imageLoadingBar;
    public Uri newestUri = null;
    EditText editText;

    public static Config getConfig() {
        return mConfig;
    }

    public static void setConfig(Config config) {

        if (config == null) {
            throw new NullPointerException("Config cannot be passed null. Not setting config will use default values.");
        }

        mConfig = config;
    }

    @Override
    public CameraHost getCameraHost() {
        return mMyCameraHost;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFromSavedInstanceState(savedInstanceState);
        setContentView(com.simons.owner.traffickcam2.R.layout.picker_activity_main_pp);
        initView();
        makeDialogue();
        setTitle(mConfig.getToolbarTitleRes());


        setupTabs();
        setSelectedPhotoRecyclerView();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            //
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            // TODO
        }

    }

    private void makeDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(ImagePickerActivity.this);
        View view = factory.inflate(R.layout.alert_dialog, null);

        dialogView = (ImageView) view.findViewById(R.id.dialog_imageview);
        dialogView.setVisibility(View.INVISIBLE);

        editText = (EditText) view.findViewById(R.id.Other);
        editText.setEnabled(false);

        checkBoxes = new CheckBox[8];
        checkBoxes[0] = (CheckBox) view.findViewById(R.id.checkBox0);
        checkBoxes[1] = (CheckBox) view.findViewById(R.id.checkBox1);
        checkBoxes[2] = (CheckBox) view.findViewById(R.id.checkBox2);
        checkBoxes[3] = (CheckBox) view.findViewById(R.id.checkBox3);
        checkBoxes[4] = (CheckBox) view.findViewById(R.id.checkBox4);
        checkBoxes[5] = (CheckBox) view.findViewById(R.id.checkBox5);
        checkBoxes[6] = (CheckBox) view.findViewById(R.id.checkBox6);
        checkBoxes[7] = (CheckBox) view.findViewById(R.id.checkBox7);

        checkBoxes[7].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    editText.setEnabled(true);
                }else
                {
                    editText.setEnabled(false);
                }
            }
        });

        imageLoadingBar = (ProgressBar) view.findViewById(R.id.progressBar);
        imageLoadingBar.setVisibility(View.VISIBLE);

        builder.setTitle("Are any of these items in this photo?")
                .setView(view)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if(newestUri!= null) addImage(newestUri);
                        imageLoadingBar.setVisibility(View.VISIBLE);
                        dialogView.setVisibility(View.INVISIBLE);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(newestUri != null) removeImage(newestUri);
                        imageLoadingBar.setVisibility(View.VISIBLE);
                        dialogView.setVisibility(View.INVISIBLE);
                    }
                });


        dialog = builder.create();

        setEnabledDialogButtons(false);

    }

    void setEnabledDialogButtons(boolean bool)
    {
        if(dialog.getButton(AlertDialog.BUTTON_POSITIVE ) != null)
        {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setEnabled(bool);
        }
        if(dialog.getButton(AlertDialog.BUTTON_NEGATIVE ) != null)
        {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setEnabled(bool);
        }
    }


    private void initView() {

        toolbar = (Toolbar) findViewById(com.simons.owner.traffickcam2.R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);


        view_root = findViewById(com.simons.owner.traffickcam2.R.id.view_root);
        mViewPager = (ViewPager) findViewById(com.simons.owner.traffickcam2.R.id.pager);
        tabLayout = (TabLayout) findViewById(com.simons.owner.traffickcam2.R.id.tab_layout);


        tv_selected_title = (TextView) findViewById(com.simons.owner.traffickcam2.R.id.tv_selected_title);

        rc_selected_photos = (RecyclerView) findViewById(com.simons.owner.traffickcam2.R.id.rc_selected_photos);
        mSelectedImageEmptyMessage = (TextView) findViewById(com.simons.owner.traffickcam2.R.id.selected_photos_empty);

        view_selected_photos_container = findViewById(com.simons.owner.traffickcam2.R.id.view_selected_photos_container);
        view_selected_photos_container.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view_selected_photos_container.getViewTreeObserver().removeOnPreDrawListener(this);

                int selected_bottom_size = (int) getResources().getDimension(mConfig.getSelectedBottomHeight());

                ViewGroup.LayoutParams params = view_selected_photos_container.getLayoutParams();
                params.height = selected_bottom_size;
                view_selected_photos_container.setLayoutParams(params);


                return true;
            }
        });


        if (mConfig.getSelectedBottomColor() > 0) {
            tv_selected_title.setBackgroundColor(ContextCompat.getColor(this, mConfig.getSelectedBottomColor()));
            mSelectedImageEmptyMessage.setTextColor(ContextCompat.getColor(this, mConfig.getSelectedBottomColor()));
        }


    }

    private void setupFromSavedInstanceState(Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            mSelectedImages = savedInstanceState.getParcelableArrayList(EXTRA_IMAGE_URIS);
        } else {
            mSelectedImages = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_URIS);
        }


        if (mSelectedImages == null) {
            mSelectedImages = new ArrayList<>();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mSelectedImages != null) {
            outState.putParcelableArrayList(EXTRA_IMAGE_URIS, mSelectedImages);
        }

    }

    private void setupTabs() {
        adapter = new PagerAdapter_Picker(this, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);


        if (mConfig.getTabBackgroundColor() > 0)
            tabLayout.setBackgroundColor(ContextCompat.getColor(this, mConfig.getTabBackgroundColor()));

        if (mConfig.getTabSelectionIndicatorColor() > 0)
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, mConfig.getTabSelectionIndicatorColor()));

    }

    private void setSelectedPhotoRecyclerView() {


        LinearLayoutManager mLayoutManager_Linear = new LinearLayoutManager(this);
        mLayoutManager_Linear.setOrientation(LinearLayoutManager.HORIZONTAL);

        rc_selected_photos.setLayoutManager(mLayoutManager_Linear);
        rc_selected_photos.addItemDecoration(new SpacesItemDecoration(Util.dpToPx(this, 5), SpacesItemDecoration.TYPE_VERTICAL));
        rc_selected_photos.setHasFixedSize(true);

        int closeImageRes = mConfig.getSelectedCloseImage();

        adapter_selectedPhoto = new Adapter_SelectedPhoto(this, closeImageRes);
        adapter_selectedPhoto.updateItems(mSelectedImages);
        rc_selected_photos.setAdapter(adapter_selectedPhoto);


        if (mSelectedImages.size() >= 1) {
            mSelectedImageEmptyMessage.setVisibility(View.GONE);
        }

    }


    public GalleryFragment getGalleryFragment() {

        if (adapter == null || adapter.getCount() < 2)
            return null;

        return (GalleryFragment) adapter.getItem(1);

    }

    public void showDialog()
    {
        /*
        imageLoadingBar.setVisibility(View.VISIBLE);
        dialogView.setVisibility(View.INVISIBLE);
        ClearCheckboxes();
        setEnabledDialogButtons(false);
        dialog.show();
        */

    }

    public void setUri(final Uri uri)
    {
        dialogView.setImageURI(uri);
        imageLoadingBar.setVisibility(View.INVISIBLE);
        dialogView.setVisibility(View.VISIBLE);
        setEnabledDialogButtons(true);
    }

    public void addImage(final Uri uri) {
        newestUri = uri;
        setUri(uri);

        if (mSelectedImages.size() == mConfig.getSelectionLimit()) {
            String text = String.format(getResources().getString(com.simons.owner.traffickcam2.R.string.max_count_msg), mConfig.getSelectionLimit());
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            return;
        }

        mSelectedImages.add(uri);
        adapter_selectedPhoto.updateItems(mSelectedImages);


        if (mSelectedImages.size() >= 1) {
            mSelectedImageEmptyMessage.setVisibility(View.GONE);
        }

        rc_selected_photos.smoothScrollToPosition(adapter_selectedPhoto.getItemCount()-1);


    }

    private void ClearCheckboxes()
    {
        int n = checkBoxes.length;
        for(int i = 0; i < n; i++) {
            if (checkBoxes[i].isChecked()) checkBoxes[i].setChecked(false);
        }

    }

    public void removeImage(Uri uri) {

        mSelectedImages.remove(uri);

        adapter_selectedPhoto.updateItems(mSelectedImages);

        if (mSelectedImages.size() == 0) {
            mSelectedImageEmptyMessage.setVisibility(View.VISIBLE);
        }
        GalleryFragment.mGalleryAdapter.notifyDataSetChanged();



    }

    public boolean containsImage(Uri uri) {
        return mSelectedImages.contains(uri);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_done) {
            updatePicture();
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    private void updatePicture() {

        if (mSelectedImages.size() < mConfig.getSelectionMin()) {
            String text = String.format(getResources().getString(com.simons.owner.traffickcam2.R.string.min_count_msg), mConfig.getSelectionMin());
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(ImagePickerActivity.this, MainActivity.class);
        //Intent intent = new Intent();
        /*
        intent.putParcelableArrayListExtra(EXTRA_IMAGE_URIS, mSelectedImages);
        setResult(Activity.RESULT_OK, intent);
        */
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_IMAGE_URIS, mSelectedImages);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
        //finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                // CLEAR CURRENT PHOTOS
                mSelectedImages.clear();
                adapter_selectedPhoto.updateItems(mSelectedImages);
                if (mSelectedImages.size() == 0) {
                    mSelectedImageEmptyMessage.setVisibility(View.VISIBLE);
                }
                GalleryFragment.mGalleryAdapter.notifyDataSetChanged();
            }
        }
    }


}
