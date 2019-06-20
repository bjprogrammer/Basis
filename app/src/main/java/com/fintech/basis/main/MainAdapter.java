package com.fintech.basis.main;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.fintech.basis.R;
import com.fintech.basis.model.BasisResponse;

import java.util.List;


public class MainAdapter extends PagerAdapter {
    private List<BasisResponse.BasisData> data;
    private Context context;
    private LayoutInflater layoutInflater;
    int[] colors;

    public MainAdapter(List<BasisResponse.BasisData> data, Context context) {
        this.data = data;
        this.context=context;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_card, container, false);
        colors=context.getResources().getIntArray(R.array.rainbow);

        CardView cardView =view.findViewById(R.id.cardView);

        int index = position% colors.length;
        cardView.setCardBackgroundColor(colors[index]);
        TextView text = view.findViewById(R.id.tv_card);
        text.setText(data.get(position).getText());

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}