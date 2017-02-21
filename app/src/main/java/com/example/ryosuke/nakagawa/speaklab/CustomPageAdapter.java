package com.example.ryosuke.nakagawa.speaklab;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ryosuke on 2017/02/21.
 */
public class CustomPageAdapter extends PagerAdapter {
    private Context mContext;
    private AssetManager mAsset;
    private String mSelectline;

    private final static int ITEM_SIZE = 5;


    //コンストラクタ
    public CustomPageAdapter(Context context,String selectLine){
        mContext = context;
        mAsset = mContext.getResources().getAssets();
        mSelectline = selectLine;
    }

    public void set(String selectline){
        mSelectline = selectline;
    }

    SparseArray<View> views = new SparseArray<View>();


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //コンテナからviewを削除
        View v = (View)object;
        ((ViewPager)container).removeView(v);
        views.remove(position);
        v=null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String item = "line_"+mSelectline+"/"+String.valueOf(position)+".jpg";

        //Viewを生成
        Bitmap bm = null;

        try{
            bm = getBitmap(item);
        } catch (IOException e){
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bm);

        container.addView(imageView);
        views.put(position,imageView);

        String advice[] ={"舌を少し前にだそう","口を横にひいて舌を上にだそう","くちびるを軽く丸めよう","","口をかるく丸め舌はおくに"};


        Toast.makeText(mContext,advice[position],Toast.LENGTH_SHORT).show();


        return imageView;
    }

    @Override
    public void notifyDataSetChanged() {
        int key = 0;
        for(int i = 0; i<views.size();i++){
            key =views.keyAt(i);
            ImageView view = (ImageView)views.get(key);

            String item = "line_"+mSelectline+"/"+String.valueOf(i)+".jpg";
            Bitmap bm = null;
            try{
                bm = getBitmap(item);
            }catch (IOException e){
                e.printStackTrace();
            }
            view.setImageBitmap(bm);
        }

        super.notifyDataSetChanged();
    }

    public Bitmap getBitmap(String item)throws IOException{
        InputStream is = mAsset.open(item);
        Bitmap bm = BitmapFactory.decodeStream(is);
        is.close();

        return bm;
    }

    @Override
    public int getCount() {
        return ITEM_SIZE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }



}
