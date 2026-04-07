package com.example.ppe4_papovo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ppe4_papovo.databinding.FragmentSecondBinding;
import android.widget.Toast;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

      return inflater.inflate(R.layout.fragment_second, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnOk = view.findViewById(R.id.bFragOk);
        Button btnCancel = view.findViewById(R.id.bFragCancel);

        // retour vers nav 1

        btnCancel.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.action_SecondFragment_to_FirstFragment);
                });

        // nav vers frag 3
        btnOk.setOnClickListener(v ->{
            ((MainActivity)getActivity()).menuConnecte();
            Navigation.findNavController(v).navigate(R.id.action_SecondFragment_to_ThirdFragment);
            Toast.makeText(getContext(), "Connexion reussie", Toast.LENGTH_SHORT).show();
        });

       /* binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

        */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}