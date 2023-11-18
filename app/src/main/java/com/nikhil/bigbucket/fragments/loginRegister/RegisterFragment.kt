package com.nikhil.bigbucket.fragments.loginRegister

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nikhil.bigbucket.R
import com.nikhil.bigbucket.data.User
import com.nikhil.bigbucket.databinding.FragmentRegisterBinding
import com.nikhil.bigbucket.util.RegisterValidation
import com.nikhil.bigbucket.util.Resource
import com.nikhil.bigbucket.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            registerButton.setOnClickListener {
                val user = User(
                    firstName.text.toString().trim(),
                    lastName.text.toString().trim(),
                    registerEmail.text.toString().trim()
                )

                d("EMAIL", user.email)
                val password = registerPassword.text.toString().trim()

                viewModel.createAccountWithEmailAndPassword(
                    user,
                    password
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    when (it) {
                        is Resource.Loading -> {
                            binding.registerButton.startAnimation()
                        }

                        is Resource.Success -> {
                            Toast.makeText(activity, "Registered Successfully", Toast.LENGTH_SHORT)
                                .show()
                            binding.registerButton.revertAnimation()
                        }

                        is Resource.Error -> {
                            Toast.makeText(activity, "Got an Failure", Toast.LENGTH_SHORT).show()
                            binding.registerButton.revertAnimation()
                        }

                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    if (validation.email is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.registerEmail.apply {
                                requestFocus()
                                error = validation.email.message
                            }
                        }
                    }

                    if (validation.password is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.registerPassword.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                        }
                    }
                }
            }
        }
    }
}