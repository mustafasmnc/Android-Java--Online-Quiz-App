package com.example.smnc.androidonlinequizapp;

import android.content.Intent;
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

import com.example.smnc.androidonlinequizapp.Common.Common;
import com.example.smnc.androidonlinequizapp.Interface.ItemClickListener;
import com.example.smnc.androidonlinequizapp.Interface.RankingCallBack;
import com.example.smnc.androidonlinequizapp.Model.QuestionScore;
import com.example.smnc.androidonlinequizapp.Model.Ranking;
import com.example.smnc.androidonlinequizapp.ViewHolder.RankingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RankingFragment extends Fragment {

    View myFragment;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking,RankingViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference questionScore,rankingTbl;

    int sum=0;

    public static RankingFragment newInstance()
    {
        RankingFragment rankingFragment=new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database=FirebaseDatabase.getInstance();
        questionScore=database.getReference("Question_Score");
        rankingTbl=database.getReference("Ranking");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment=inflater.inflate(R.layout.fragment_ranking,container,false);

        //Inıt View
        rankingList=(RecyclerView)myFragment.findViewById(R.id.rankingList);
        layoutManager=new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);
        //Because OrderByChild method of Firebase will sort list with ascending
        //so we need reserve our Recyler data
        // by layout manager
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);

        // now, we need implemet callback
        updateScore(Common.currentUser.getUserName(), new RankingCallBack<Ranking>() {
            @Override
            public void callBack(Ranking ranking) {
                //update to ranking table
                rankingTbl.child(ranking.getUserName()).setValue(ranking);
               // showRanking(); //after upload we will sort ranking table and show result

            }
        });

        //set adapter
        adapter=new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class,
                R.layout.layout_ranking,
                RankingViewHolder.class,
                rankingTbl.orderByChild("score")
        )//2. orderByChild YOU HAVE  TO ADD RULES İN FİREBASE
        {
            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, final Ranking model, int position) {
                viewHolder.txt_name.setText(model.getUserName());
                viewHolder.txt_score.setText(String.valueOf(model.getScore()));

                //fixed crash when click to item
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent scoreDetail=new Intent(getActivity(),ScoreDetail.class);
                        scoreDetail.putExtra("viewUser",model.getUserName());
                        startActivity(scoreDetail);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);

        return myFragment;
    }

    private void showRanking() {
        //print log to show
        rankingTbl.orderByChild("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Ranking local = data.getValue(Ranking.class);
                    Log.d("DEBUG", String.valueOf(local.getScore()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateScore(final String userName, final RankingCallBack<Ranking> callback) {
        questionScore.orderByChild("user").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() { //1. orderByChild YOU HAVE  TO ADD RULES İN FİREBASE
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    QuestionScore ques=data.getValue(QuestionScore.class);
                    sum+=Integer.parseInt(ques.getScore());
                }
                // after sumary all score, we need process sum variable here
                // becaause  firebase is async db, so if process outside,
                // our 'sum' value will be reset to 0
                Ranking ranking=new Ranking(userName,sum);
                callback.callBack(ranking);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
