package com.example.apploginizi;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// Clase JavaMail que maneja el envío de correos electrónicos utilizando SMTP
public class JavaMail {

    // Configuración del servidor SMTP
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String USERNAME = "@gmail.com"; // Dirección de correo electrónico del remitente
    private static final String PASSWORD = "Contraseña de aplicación";// Contraseña de aplicación

    // Método para enviar un correo electrónico de verificación
    public void sendVerificationEmail(String toEmail, String subject, String verificationCode) {
        new SendMailTask().execute(toEmail, subject, verificationCode);
    }

    // Clase interna que extiende AsyncTask para manejar el envío de correos electrónicos en segundo plano
    private class SendMailTask extends AsyncTask<String, Void, Boolean> {

        // Método que se ejecuta en segundo plano
        @Override
        protected Boolean doInBackground(String... params) {
            String toEmail = params[0];
            String subject = params[1];
            String verificationCode = params[2];

            // Configuración de las propiedades del correo electrónico
            Properties props = new Properties();
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", HOST);

            // Crea una sesión de correo con autenticación
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(USERNAME, PASSWORD); // Autenticación con el correo y contraseña del remitente
                        }
                    });

            try {
                // Crea un nuevo mensaje de correo
                Message message = new MimeMessage(session);

                // Establece el remitente solo con un nombre personalizado
                message.setFrom(new InternetAddress(USERNAME));

                // Establece los destinatarios
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

                // Establece el asunto del correo
                message.setSubject(subject);

                // Construye el cuerpo del mensaje con una mejor presentación
                String mensajePersonalizado = "Hola,\n\n" +
                        "Gracias por confiar en La Tía Veneno. Tu seguridad es nuestra prioridad.\n\n" +
                        "Aquí tienes tu código de verificación para continuar con el proceso:\n\n" +
                        "Código de verificación: " + verificationCode + "\n\n" +
                        "Si no solicitaste este código, por favor ignora este correo.\n\n" +
                        "Saludos cordiales,\n" +
                        "El equipo de La Tía Veneno";

                // Establece el cuerpo del mensaje
                message.setText(mensajePersonalizado);

                // Envía el correo electrónico
                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                Log.e("JavaMail", "Error al enviar correo electrónico", e);
                return false;
            }

        }

        // Método que se ejecuta después de finalizar la tarea en segundo plano
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d("JavaMail", "Correo electrónico enviado correctamente");
            } else {
                Log.e("JavaMail", "\n" + "No se pudo enviar el correo electrónico");
            }
        }
    }
}