package itms.com.pe.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class fr_sin_atenciones extends Fragment {

    View v;

    public fr_sin_atenciones() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Mis Atenciones");
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fr_sin_atenciones, container, false);
        return v;
    }

}
