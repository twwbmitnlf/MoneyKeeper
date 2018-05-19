package me.bakumon.moneykeeper.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import me.bakumon.moneykeeper.R;
import me.bakumon.moneykeeper.database.entity.RecordType;
import me.bakumon.moneykeeper.database.entity.RecordWithType;
import me.bakumon.moneykeeper.databinding.LayoutTypePageBinding;
import me.bakumon.moneykeeper.ui.add.TypeAdapter;
import me.bakumon.moneykeeper.view.pagerlayoutmanager.PagerGridLayoutManager;
import me.bakumon.moneykeeper.view.pagerlayoutmanager.PagerGridSnapHelper;

/**
 * 翻页的 recyclerView + 指示器
 *
 * @author Bakumon https://bakumon.me
 */
public class TypePageView extends LinearLayout {

    private static final int ROW = 2;
    private static final int COLUMN = 4;

    private LayoutTypePageBinding mBinding;
    private TypeAdapter mAdapter;
    private int mCurrentTypeIndex = -1;

    public TypePageView(Context context) {
        this(context, null);
    }

    public TypePageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypePageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_type_page, this, true);

        // 1.水平分页布局管理器
        PagerGridLayoutManager layoutManager = new PagerGridLayoutManager(
                ROW, COLUMN, PagerGridLayoutManager.HORIZONTAL);
        mBinding.recyclerView.setLayoutManager(layoutManager);

        // 2.设置滚动辅助工具
        PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
        pageSnapHelper.attachToRecyclerView(mBinding.recyclerView);


        mAdapter = new TypeAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            mAdapter.clickItem(position);
            mCurrentTypeIndex = position;
        });
        mBinding.recyclerView.setAdapter(mAdapter);

        layoutManager.setPageListener(new PagerGridLayoutManager.PageListener() {
            int currentPageIndex;
            int pageSize;

            @Override
            public void onPageSizeChanged(int pageSize) {
                this.pageSize = pageSize;
                setIndicator();
            }

            @Override
            public void onPageSelect(int pageIndex) {
                currentPageIndex = pageIndex;
                setIndicator();
            }

            private void setIndicator() {
                if (pageSize > 1) {
                    mBinding.indicator.setVisibility(View.VISIBLE);
                    mBinding.indicator.setTotal(pageSize, currentPageIndex);
                } else {
                    mBinding.indicator.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void setNewData(@Nullable List<RecordType> data, int type) {
        mAdapter.setNewData(data, type);
    }

    /**
     * 该方法只改变一次
     */
    public void initCheckItem(RecordWithType record) {
        if (mCurrentTypeIndex == -1) {
            mCurrentTypeIndex = 0;
            int size = mAdapter.getData().size();
            if (record != null && size > 0) {
                for (int i = 0; i < size; i++) {
                    if (record.mRecordTypes.get(0).id == mAdapter.getData().get(i).id) {
                        mCurrentTypeIndex = i;
                    }
                }
            }
            mAdapter.clickItem(mCurrentTypeIndex);
        }
    }

    public RecordType getCurrentItem() {
        return mAdapter.getCurrentItem();
    }

}