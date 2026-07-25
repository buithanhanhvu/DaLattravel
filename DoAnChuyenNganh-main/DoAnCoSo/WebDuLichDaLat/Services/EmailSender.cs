using Microsoft.AspNetCore.Identity.UI.Services;
using Microsoft.Extensions.Options;
using System.Net;
using System.Net.Mail;
using System.Threading.Tasks;
using WebDuLichDaLat.Models;

namespace WebDuLichDaLat.Services
{
    public class EmailSender : IEmailSender
    {
        private readonly EmailSettings _emailSettings;
        private readonly ILogger<EmailSender> _logger;

        public EmailSender(IOptions<EmailSettings> emailSettings, ILogger<EmailSender> logger)
        {
            _emailSettings = emailSettings.Value;
            _logger = logger;
        }

        public async Task SendEmailAsync(string email, string subject, string htmlMessage)
        {
            // Kiểm tra cấu hình có hợp lệ không
            if (string.IsNullOrEmpty(_emailSettings.FromEmail) || string.IsNullOrEmpty(_emailSettings.SmtpServer))
            {
                _logger.LogWarning("Canh bao: Chua cau hinh SMTP trong appsettings.json. Khong the gui email cho: {Email}", email);
                return;
            }

            try
            {
                var message = new MailMessage
                {
                    From = new MailAddress(_emailSettings.FromEmail, _emailSettings.DisplayName),
                    Subject = subject,
                    Body = htmlMessage,
                    IsBodyHtml = true
                };
                message.To.Add(email);

                using var smtpClient = new SmtpClient(_emailSettings.SmtpServer)
                {
                    Port = _emailSettings.Port,
                    Credentials = new NetworkCredential(_emailSettings.Username, _emailSettings.Password),
                    EnableSsl = true
                };

                await smtpClient.SendMailAsync(message);
                _logger.LogInformation("Email da duoc gui thanh cong cho: {Email}", email);
            }
            catch (SmtpException ex)
            {
                _logger.LogError("Loi gui Email (SMTP): {Message}. Vui long kiem tra cau hinh 'EmailSettings' trong appsettings.json.", ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError("Loi he thong khi gui Email: {Message}", ex.Message);
            }
        }
    }
}

