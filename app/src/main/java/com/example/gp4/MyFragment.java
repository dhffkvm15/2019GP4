package com.example.gp4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.bind.ArrayTypeAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 나의 향 정보
public class MyFragment extends Fragment {

    public static MyFragment newInstance(){
        return new MyFragment();
    }
    private Boolean isWork = false; // 디퓨저가 작동 중인지 저장할 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.fragment_my_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new MyFragmentRecyclerViewAdapter());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        isWork = sharedPreferences.getBoolean("turnOn", false); // 디퓨저 작동하는지 가져오기

        return view;
    }

    class MyFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<TotalInfo> totalInfos;
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String key = sharedPreferences.getString("pushID", ""); // 저장되어 있는 키 값 불러오기

        public MyFragmentRecyclerViewAdapter(){

            totalInfos = new ArrayList<>();


            ValueEventListener valueEventListener = FirebaseDatabase.getInstance().getReference("storage").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    totalInfos.clear(); // 누적된 데이터 클리어
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Log.v("태그", "태그 데이터 확인 : " + snapshot.getValue());
                        TotalInfo tmptotal = snapshot.getValue(TotalInfo.class);
                        totalInfos.add(tmptotal); // 리스트에 추가
                    }
                    notifyDataSetChanged(); //새로고침
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_scentinfo, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

            //텍스트 올리기
            ((CustomViewHolder)viewHolder).nameview.setText(totalInfos.get(i).getName());

            String string = totalInfos.get(i).getCatridgeInfo1().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo1().getRest())
                    +" " + totalInfos.get(i).getCatridgeInfo2().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo2().getRest())
                    +" " + totalInfos.get(i).getCatridgeInfo3().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo3().getRest())
                    +" " + totalInfos.get(i).getCatridgeInfo4().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo4().getRest())
                    +"\n" + totalInfos.get(i).getCatridgeInfo5().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo5().getRest())
                    +" " + totalInfos.get(i).getCatridgeInfo6().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo6().getRest());

            ((CustomViewHolder)viewHolder).infoview.setText(string);

            // 클릭 시 작동하도록 하기
            if(isWork){
                viewHolder.itemView.setEnabled(false);
            }else{
                viewHolder.itemView.setEnabled(true);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String temp = "2/2/2/2/2/2";

                        editor.putString("before", temp);
                        editor.commit(); // 이전에 켰었던 정보 저장

                        Bundle bundle = new Bundle();
                        Fragment turnon2fragment = new Turnon2Fragment();
                        bundle.putSerializable("total", totalInfos.get(i));
                       turnon2fragment.setArguments(bundle);
                       ((MainActivity)getActivity()).replaceFragment(turnon2fragment);
//                        Intent intent = new Intent(getActivity(), PlayDiffuserActivity.class);
//                        intent.putExtra("val", (Serializable) totalInfos.get(i));
//
//                        startActivity(intent);

                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return totalInfos.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView nameview;
            public TextView infoview;

            public CustomViewHolder(View view) {
                super(view);
                nameview = (TextView)view.findViewById(R.id.item_scentinfo_name);
                infoview = (TextView)view.findViewById(R.id.item_scentinfo_info);
            }
        }
    }

    private String whatString(int rest) {
        String tmp = "";
        switch (rest){
            case 0:
                tmp = "0% ";
                break;
            case 1:
                tmp = "50% ";
                break;
            case 2:
                tmp = "100% ";
                break;
        }
        return tmp;
    }
}
