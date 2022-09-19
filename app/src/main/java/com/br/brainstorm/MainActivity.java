package com.br.brainstorm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Repository>>{

    private ListView dataListView;
    private EditText requestTag;
    private TextView errorMessage;
    private ProgressBar loadingBar;
    private RepositoryAdapter adapter;

    private static final int GITHUB_SEARCH_LOADER = 1;
    private static final String GITHUB_QUERY_TAG = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingBar =findViewById(R.id.loadingBar);
        errorMessage = findViewById(R.id.errorMessage);
        requestTag = findViewById(R.id.requestTag);

        dataListView = findViewById(R.id.dataListView);
        dataListView.setEmptyView(errorMessage);

        adapter = new RepositoryAdapter(getApplicationContext());
        dataListView.setAdapter(adapter);

        if(savedInstanceState != null){
            String  queryUrl =  savedInstanceState.getString(GITHUB_QUERY_TAG);
            requestTag.setText(queryUrl);
        }
        getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(GITHUB_QUERY_TAG, requestTag.getText().toString().trim());
    }

    public Loader<List<Repository>> onCreateLoader(int id, final Bundle args){
        return new AsyncTaskLoader<List<Repository>>(this) {
            List<Repository> mRepositoryList;

            @Override
            protected void onStartLoading() {
                if(args == null){
                    return;
                }
                loadingBar.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.INVISIBLE);
                if(mRepositoryList != null){
                    deliverResult(mRepositoryList);
                }else{
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Repository> loadInBackground() {
                String searchQueryUrlString = args.getString(GITHUB_QUERY_TAG);

                try{
                    List<Repository> githubSearchResults = NetworkUtils.getDataFromApi(searchQueryUrlString);
                    return githubSearchResults;
                }catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable List<Repository> githubJson) {
                mRepositoryList = githubJson;
                super.deliverResult(githubJson);
            }
        };
    }

    public void onLoadFinished(Loader<List<Repository>> loader, List<Repository> data){
        loadingBar.setVisibility(View.INVISIBLE);
        if(data==null){
            showErrorMessage();
        }else{
            adapter.clear();
            adapter.addAll(data);
            showJsonDataView();
        }
    }
    public void onLoaderReset(Loader<List<Repository>> loader){

    }

    private void showJsonDataView() {
        errorMessage.setVisibility(View.INVISIBLE);
        dataListView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        dataListView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    public void searchRepo(View view) {
        makeGithubSearchQuery();
    }

    private void makeGithubSearchQuery() {
        String githubQuery = requestTag.getText().toString();

        Bundle queryBundle = new Bundle();
        queryBundle.putString(GITHUB_QUERY_TAG, githubQuery);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);
        if(githubSearchLoader == null){
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        }else{
            loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        }
    }
}