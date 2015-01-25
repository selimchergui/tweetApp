package fr.ecp.sio.superchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import fr.ecp.sio.superchat.loaders.UsersLoader;
import fr.ecp.sio.superchat.model.User;

/**
 * Created by Michaël on 05/12/2014.
 */
public class UsersFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<User>> {

    private static final int LOADER_USERS = 1000;
    private static final int REQUEST_LOGIN_FOR_POST = 1;

    private UsersAdapter mListAdapter;
    private boolean mIsMasterDetailsMode;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.users_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListAdapter = new UsersAdapter();
        setListAdapter(mListAdapter);
        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    String handle = AccountManager.getUserHandle(getActivity());
                    mListAdapter.setFollowingsHandles(new ApiClient().getFollowingsHandles(handle));
                    return true;
                } catch (IOException e) {
                    Log.e(MainActivity.class.getName(), "Post failed", e);
                    return false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;

                }
            }
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                } else {
                }
            }

        }.execute();
        view.findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsMasterDetailsMode = getActivity().findViewById(R.id.tweets_content) != null;
        if (mIsMasterDetailsMode) {
            getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        }

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {

                User user = mListAdapter.getItem(position);
                follow(user.getHandle());
                return true;
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOADER_USERS, null, this);


    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new UsersLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> users) {
        mListAdapter.setUsers(users);

    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) { }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        User user = mListAdapter.getItem(position);
        if (mIsMasterDetailsMode) {
            Fragment tweetsFragment = new TweetsFragment();
            tweetsFragment.setArguments(TweetsFragment.newArguments(user));
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.tweets_content, tweetsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), TweetsActivity.class);
            intent.putExtras(TweetsFragment.newArguments(user));



            startActivity(intent);
        }
    }

    private void post() {
        if (AccountManager.isConnected(getActivity())) {
            startActivity(new Intent(getActivity(), PostActivity.class));
        } else {
            LoginFragment fragment = new LoginFragment();
            fragment.setTargetFragment(this, REQUEST_LOGIN_FOR_POST);
            fragment.show(getFragmentManager(), "login_dialog");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_FOR_POST && resultCode == PostActivity.RESULT_OK) {
            startActivity(new Intent(getActivity(), PostActivity.class));
        }
    }



    public void follow(final String userToFollow) {


        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    String userToFollow = params[0];
                    String handle = AccountManager.getUserHandle(getActivity());
                    String token = AccountManager.getUserToken(getActivity());
                    new ApiClient().follow(handle, token,userToFollow);
                    return true;
                } catch (IOException e) {
                    Log.e(MainActivity.class.getName(), "Post failed", e);
                    return false;
                }
            }
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    Toast.makeText(getActivity(), userToFollow+"followed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.post_error, Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(userToFollow);}

}