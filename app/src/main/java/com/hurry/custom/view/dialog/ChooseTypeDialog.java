package com.hurry.custom.view.dialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.IPositiveButtonDialogListener;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.view.activity.CameraOrderActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.MainActivity;

import static com.hurry.custom.view.activity.HomeActivity.CAMERA_ORDER;
import static com.hurry.custom.view.activity.HomeActivity.ITEM_ORDER;

/**
 * Sample implementation of custom dialog by extending {@link SimpleDialogFragment}.
 *
 * @author David Vávra (david@inmite.eu)
 */
public class ChooseTypeDialog extends SimpleDialogFragment {

    public static String TAG = "jayne";

    private static final String[] INITIAL_PERMS={
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int INITIAL_REQUEST=1337;
    public static String type;
    public static void show(FragmentActivity activity, String type) {

        ChooseTypeDialog dialog = new ChooseTypeDialog();

        try{
            dialog.getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation_2);
        }catch (Exception e){

        }


        dialog.show(activity.getSupportFragmentManager(), TAG);

        try{

            dialog.getActivity().getWindow().setWindowAnimations(R.style.DialogAnimation_2);
            dialog.getActivity().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;

        }catch (Exception e){

        }


        ChooseTypeDialog.type = type;
    }

    @Override
    public int getTheme() {
        return R.style.JayneHatDialogTheme;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() == null){
            return;
        }
        getDialog().getWindow().setWindowAnimations(
                R.style.DialogAnimation_2);
    }

    @Override
    public BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder) {
        //builder.setTitle("Select an option to book a delivery");
        //builder.setMessage("A man walks down the street in that hat, people know he's not afraid of anything.");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delivery_type, null);
        builder.setView(view);

        builder.setPositiveButton("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (IPositiveButtonDialogListener listener : getPositiveButtonDialogListeners()) {
                    listener.onPositiveButtonClicked(mRequestCode);
                }
                dismiss();
            }
        });

        LinearLayout linCamera = (LinearLayout)view.findViewById(R.id.lin_camera);
        LinearLayout linPackage = (LinearLayout)view.findViewById(R.id.lin_package);
        linCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.ORDER_TYPE = Constants.CAMERA_OPTION;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED ) {
                        Constants.ORDER_TYPE = Constants.CAMERA_OPTION;
                        ((HomeActivity)getActivity()).goToCameraPage(type);

//                        Intent intent = new Intent(getActivity(), CameraOrderActivity.class);
//                        intent.putExtra("type", type);
//                        startActivity(intent);

                    } else {
                        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                    }
                }
                dismiss();
            }
        });

        linPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.ORDER_TYPE = Constants.ITEM_OPTION;
                ((HomeActivity)getActivity()).updateFragment(ITEM_ORDER, type);

//                Intent itemOrder = new Intent(getActivity(), ItemOrderActivity.class);
//                itemOrder.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                itemOrder.putExtra("type", type);
//                startActivity(itemOrder);

                dismiss();
            }
        });
        return builder;
    }

    private void getImages(Config config) {
        config.setSelectionLimit(CameraOrderActivity.MAX - Constants.cameraOrderModel.itemModels.size());
        if(DeviceUtil.isTablet(getActivity())){
            config.setCameraHeight(R.dimen.camera_tablet);
        }else{
            config.setCameraHeight(R.dimen.camera_phone);
        }
        //ImagePickerActivity.setConfig(config);
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra("limit", CameraOrderActivity.MAX - Constants.cameraOrderModel.itemModels.size());
        getActivity().startActivityForResult(intent, MainActivity.INTENT_REQUEST_GET_IMAGES);
    }
}