package com.example.autofill.ui.main;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autofill.R;
import com.example.autofill.storage.InformationDao;
import com.example.autofill.storage.InformationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 复制自informationListAdapter并作改编
 */
public class MrdkListAdapter extends RecyclerView.Adapter<MrdkListAdapter.MrdkViewHolder> {

    private List<InformationEntity> information;
    private Context context;
    private static final String TAG = "MrdkListAdapter成功";
    private ArrayList<MrdkCacheHolder> dataCacheHolder;
    public static String xh = "";

    private static final int PHOTO = 0;
    private static final int RADIO = 2;
    private static final int EDIT = 1;

    public MrdkListAdapter(Context context,ArrayList<MrdkCacheHolder> mrdkCacheHolder){
        this.context = context;
        this.dataCacheHolder = mrdkCacheHolder;
    }


    /**
     * 用来暂存的内部类
     */
    class MrdkViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        private final TextView titleItemView;
        private ImageView mrdkImage;
        private EditText editInfo;
        private RadioGroup selectGroup;
        private RadioButton buttonYes;
        private RadioButton buttonNo;

        public MrdkViewHolder(@NonNull View itemView) {
            super(itemView);
            //Log.d(TAG, "MrdkViewHolder: ");
            this.itemView = itemView;
            this.titleItemView = itemView.findViewById(R.id.mrdk_title);
            this.mrdkImage = itemView.findViewById(R.id.mrdk_image);
            this.selectGroup = itemView.findViewById(R.id.select_radiogroup);
            this.editInfo = itemView.findViewById(R.id.edit_data);
            this.buttonYes = itemView.findViewById(R.id.radiobutton_yes);
            this.buttonNo = itemView.findViewById(R.id.radiobutton_no);
        }
    }


    @NonNull
    @Override
    public MrdkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.mrdk_recycler_item,parent,false);
        return new MrdkListAdapter.MrdkViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MrdkViewHolder holder, final int position) {

        final InformationEntity currentEntity = information.get(position);

        final MrdkCacheHolder cacheHolder = dataCacheHolder.get(position);

        //Log.d(TAG, "onBindViewHolder:测试 "+cacheHolder.getVisibility());

        //缓存学号供查询
        if(position==1){
            xh = parseStation(currentEntity.getStation());
        }

        //设置可见性
        switch (cacheHolder.getVisibility()){
            case PHOTO:
                holder.mrdkImage.setVisibility(View.VISIBLE);
                holder.titleItemView.setVisibility(View.GONE);
                holder.editInfo.setVisibility(View.GONE);
                holder.selectGroup.setVisibility(View.GONE);
                break;

            case EDIT:
                holder.mrdkImage.setVisibility(View.GONE);
                holder.titleItemView.setVisibility(View.VISIBLE);
                holder.editInfo.setVisibility(View.VISIBLE);
                holder.selectGroup.setVisibility(View.GONE);
                break;

            case RADIO:
                holder.mrdkImage.setVisibility(View.GONE);
                holder.titleItemView.setVisibility(View.VISIBLE);
                holder.editInfo.setVisibility(View.GONE);
                holder.selectGroup.setVisibility(View.VISIBLE);
                break;

            default:
                holder.mrdkImage.setVisibility(View.VISIBLE);
                holder.titleItemView.setVisibility(View.VISIBLE);
                holder.editInfo.setVisibility(View.VISIBLE);
                holder.selectGroup.setVisibility(View.VISIBLE);
        }


        //Log.d(TAG, "onBindViewHolder: 数据"+currentEntity.getStation());
        //加载标题
        if(holder.titleItemView.getVisibility()==View.VISIBLE){
            holder.titleItemView.setText(cacheHolder.getTitle()+":");
        }

        //加载图片
        if (holder.mrdkImage.getVisibility()==View.VISIBLE){
            Glide.with(context).load(cacheHolder.getImage()).into(holder.mrdkImage);
        }

        /**
         * 设置监听
         */
        if (holder.editInfo.getVisibility() == View.VISIBLE) {
            //Log.d(TAG, "onBindViewHolder: edit info的可见性");
            holder.editInfo.setHint(cacheHolder.getContentHint());
            holder.editInfo.setText(parseStation(currentEntity.getStation()));
            holder.editInfo.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        String s = "";
                        s =   holder.editInfo.getText().toString();
                        //检查s是否为空，防止崩溃
                        if(s!=""&&s!=null) {
                            Intent intent = new Intent(context, UpdataActivity.class);
                            intent.putExtra("id", currentEntity.getId());
                            intent.putExtra("changedData", s);
                            intent.putExtra("unparsedStation", currentEntity.getStation());
                            context.startActivity(intent);
                            //Log.d(TAG, "onKey:"+s);
                        }
                    }
                    return false;
                }
            });
        }

        if(holder.selectGroup.getVisibility() == View.VISIBLE){
           // Log.d(TAG, "onBindViewHolder: select group的可见性");
            holder.buttonYes.setText(cacheHolder.getDataYes());
            holder.buttonNo.setText(cacheHolder.getDataNo());

            holder.selectGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.radiobutton_yes:
                            Intent intentYes = new Intent(context,UpdataActivity.class);
                            intentYes.putExtra("id",currentEntity.getId());
                            intentYes.putExtra("changedData",cacheHolder.getDataYes());
                            intentYes.putExtra("unparsedStation",currentEntity.getStation());
                            context.startActivity(intentYes);

                            break;
                        case R.id.radiobutton_no:
                            Intent intentNo = new Intent(context,UpdataActivity.class);
                            intentNo.putExtra("id",currentEntity.getId());
                            intentNo.putExtra("changedData",cacheHolder.getDataNo());
                            intentNo.putExtra("unparsedStation",currentEntity.getStation());
                            context.startActivity(intentNo);
                            break;
                        default:
                            Log.d(TAG, "onCheckedChanged: radio group成了default");


                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(information!=null){
            return information.size();
        }else
            return 0;
    }

    String parseStation(String unparsedStation){
        String[] splitted = unparsedStation.split("-");

        /**该循环验证字符串是否按想要的方式分裂
        for (int i = 0; i < splitted.length; i++) {
            Log.d(TAG, "分裂字符串"+splitted[i]+"循环节i："+i);
        }
         */
        return splitted[1];
    }


    void setData(List<InformationEntity> data) {
        information = data;
        notifyDataSetChanged();
    }

}
