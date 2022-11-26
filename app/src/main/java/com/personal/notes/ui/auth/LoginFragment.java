package com.personal.notes.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.personal.notes.R;
import com.personal.notes.data.entities.User;
import com.personal.notes.databinding.FragmentLoginBinding;
import com.personal.notes.ui.MainActivity;

public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    LoginFragmentClickHandlers loginFragmentClickHandlers;
    private AuthViewModel authViewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        binding = DataBindingUtil.bind(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        authViewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
        loginFragmentClickHandlers = new LoginFragmentClickHandlers(getActivity());
        binding.setLoginClickHandler(loginFragmentClickHandlers);
        binding.setAuthViewModel(authViewModel);

    }

    private void loginUser() {
        if (!authViewModel.isMobile()) {
            authViewModel.loginWithEmail().observe(getActivity(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null && !isDetached() && user.getPassword().equals(authViewModel.md5(authViewModel.getPassword()))) {
                        authViewModel.addSession(user.userId);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        if (getActivity() != null)
                            getActivity().finish();
                    } else
                        Toast.makeText(getActivity(), "Invalid Credentials", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            authViewModel.loginWithMobile().observe(getActivity(), new Observer<User>() {
                @Override
                public void onChanged(User user) {

                    if (user != null && !isDetached() && user.getPassword().equals(authViewModel.md5(authViewModel.getPassword()))) {
                        authViewModel.addSession(user.userId);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        if (getActivity() != null)
                            getActivity().finish();
                    } else
                        Toast.makeText(getActivity(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class LoginFragmentClickHandlers {
        Context context;

        public LoginFragmentClickHandlers(Context context) {
            this.context = context;
        }

        public void onLoginClicked(View view) {
            loginUser();
        }
    }


}