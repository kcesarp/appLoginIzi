package com.example.apploginizi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Este Activity permite a los usuarios verificar su identidad y restablecer su contraseña
// utilizando un código de verificación enviado por correo electrónico
public class VerificacionActivity extends AppCompatActivity {

    // Declaración de los elementos de la interfaz de usuario y clases auxiliares
    private EditText editTextEmail, editTextCodigo, editTextNuevaContrasena;
    private Button buttonEnviarCodigo, buttonVerificar;
    private Administrador dbHelper;
    private JavaMail javaMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion);

        // Inicialización de las instancias de las clases de ayuda
        dbHelper = new Administrador(this);
        javaMail = new JavaMail();

        // Inicialización de los elementos de la interfaz de usuario
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCodigo = findViewById(R.id.editTextCodigo);
        editTextNuevaContrasena = findViewById(R.id.editTextNuevaContrasena);
        buttonEnviarCodigo = findViewById(R.id.buttonEnviarCodigo);
        buttonVerificar = findViewById(R.id.buttonVerificar);

        // Obtiene el correo electrónico pasado desde la actividad anterior y lo establece en el campo de correo
        String email = getIntent().getStringExtra("EMAIL");
        if (email != null && !email.isEmpty()) {
            editTextEmail.setText(email);
            editTextEmail.setEnabled(false);
        }

        // Configura el botón para enviar el código de verificación por correo electrónico
        buttonEnviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarCodigoVerificacion();
            }
        });

        // Configura el botón para verificar el código de verificación introducido por el usuario
        buttonVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCodigo();
            }
        });
    }

    // Método para enviar el código de verificación al correo electrónico del usuario
    private void enviarCodigoVerificacion() {
        String email = editTextEmail.getText().toString().trim();

        // Verifica que el campo de correo no esté vacío
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica si el usuario existe en la base de datos
        if (dbHelper.checkUserExists(email)) {
            // Genera un código de verificación aleatorio y lo guarda en la base de datos
            String codigoVerificacion = generarCodigoVerificacion();
            dbHelper.guardarCodigoVerificacion(email, codigoVerificacion);

            // Envía el código de verificación por correo electrónico
            javaMail.sendVerificationEmail(email, "La Tía Veneno - Código de verificación", codigoVerificacion);
            Toast.makeText(this, "Se ha enviado un código de verificación a su correo electrónico", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se encontró ningún usuario con ese correo electrónico", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para verificar el código de verificación introducido por el usuario y actualizar la contraseña si es correcto
    private void verificarCodigo() {
        String email = editTextEmail.getText().toString().trim();
        String codigo = editTextCodigo.getText().toString().trim();
        String nuevaContrasena = editTextNuevaContrasena.getText().toString().trim();

        // Verifica que todos los campos estén completos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(codigo) || TextUtils.isEmpty(nuevaContrasena)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica si el código de verificación es correcto
        if (dbHelper.verificarCodigo(email, codigo)) {
            // Actualiza la contraseña del usuario en la base de datos
            dbHelper.actualizarContrasena(email, nuevaContrasena);
            Toast.makeText(this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Código de verificación incorrecto", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para generar un código de verificación aleatorio de 6 dígitos
    private String generarCodigoVerificacion() {
        return String.valueOf(100000 + new java.util.Random().nextInt(900000));
    }
}