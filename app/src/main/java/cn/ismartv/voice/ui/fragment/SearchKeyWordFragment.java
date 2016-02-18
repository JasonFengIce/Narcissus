package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.ismartv.voice.R;

/**
 * Created by huaijie on 2/18/16.
 */
public class SearchKeyWordFragment extends BaseFragment {
    private TextView searchKeyWordText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_keyword, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchKeyWordText = (TextView) view.findViewById(R.id.search_keyword);
    }

    public void setSearchKeyWord(String keyWord) {
        searchKeyWordText.setText(keyWord);
    }


}
