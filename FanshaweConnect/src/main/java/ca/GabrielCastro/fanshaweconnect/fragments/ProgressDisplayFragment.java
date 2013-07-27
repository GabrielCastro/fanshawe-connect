package ca.GabrielCastro.fanshaweconnect.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.GabrielCastro.fanshaweconnect.R;

public class ProgressDisplayFragment extends Fragment {

    private Callbacks mCallbacks;
    private View mView;

    public static ProgressDisplayFragment newInstance() {
        return new ProgressDisplayFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getParentFragment() instanceof Callbacks) {
            mCallbacks = (Callbacks) getParentFragment();
        } else if (activity instanceof Callbacks) {
            mCallbacks = (Callbacks) activity;
        } else {
            throw new IllegalStateException("no callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_progress, container, false);
        } else {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
        return mView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static interface Callbacks {

    }
}
