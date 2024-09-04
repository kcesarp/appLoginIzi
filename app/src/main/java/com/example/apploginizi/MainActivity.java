package com.example.apploginizi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

// MainActivity de la aplicación, donde los usuarios pueden iniciar sesión, registrarse o recuperar su contraseña
public class MainActivity extends AppCompatActivity {

    // Declaración de los elementos de la interfaz de usuario y de las clases auxiliares
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister, textViewForgotPassword;
    private Administrador dbHelper;
    private JavaMail javaMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de las instancias de las clases de ayuda
        dbHelper = new Administrador(this);
        javaMail = new JavaMail();

        // Inicialización de los elementos de la interfaz de usuario
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        // Configuración del botón de inicio de sesión
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtención de las credenciales del usuario
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                // Verificación de las credenciales y redirección a la actividad de bienvenida si son correctas
                if (dbHelper.checkUser(email, password)) {
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    intent.putExtra("EMAIL", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configuración del enlace de registro de usuario
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        // Configuración del enlace de recuperación de contraseña
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperarContrasena();
            }
        });
    }

    // Método para registrar un nuevo usuario
    private void registrarUsuario() {
        // Obtiene y valida los datos del usuario
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica si el usuario ya existe en la base de datos
        if (dbHelper.checkUserExists(email)) {
            Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
        } else {
            boolean result = dbHelper.addUser(email, password);
            if (result) {
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para recuperar la contraseña
    private void recuperarContrasena() {
        // Obtiene el correo electrónico del usuario
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica si el usuario existe en la base de datos
        if (dbHelper.checkUserExists(email)) {
            // Genera un código de verificación y lo guarda en la base de datos
            String codigoVerificacion = generarCodigoVerificacion();
            dbHelper.guardarCodigoVerificacion(email, codigoVerificacion);

            // Envía el código de verificación por correo electrónico
            javaMail.sendVerificationEmail(email, "La Tía Veneno - Código de verificación", codigoVerificacion);
            Toast.makeText(this, "Se ha enviado un código de verificación a su correo electrónico", Toast.LENGTH_LONG).show();

            // Redirige a la actividad de verificación de código
            Intent intent = new Intent(MainActivity.this, VerificacionActivity.class);
            intent.putExtra("EMAIL", email);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No se encontró ningún usuario con ese correo electrónico", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para generar un código de verificación aleatorio de 6 dígitos
    private String generarCodigoVerificacion() {
        return String.valueOf(100000 + new java.util.Random().nextInt(900000));
    }
}