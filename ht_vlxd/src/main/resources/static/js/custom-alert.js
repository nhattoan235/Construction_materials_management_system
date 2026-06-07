(function() {
  // Save original alert just in case
  const originalAlert = window.alert;

  // Custom alert wrapper
  window.alert = function(message, callback) {
    const existing = document.getElementById('custom-alert-overlay');
    if (existing) {
      existing.remove();
    }
    
    // Get current theme role to adjust theme colors dynamically if needed
    const role = localStorage.getItem('demo_role') || 'KHACH_HANG';
    
    // Choose primary colors matching CSS style.css tokens
    let themeColor = '#76a89c'; // Default customer (Teal)
    let lightBg = '#e6f0ed';
    
    if (role === 'NV_KINH_DOANH') {
      themeColor = '#10b981';
      lightBg = '#ecfdf5';
    } else if (role === 'NV_KE_TOAN') {
      themeColor = '#f59e0b';
      lightBg = '#fffbeb';
    } else if (role === 'BAN_QUAN_LY') {
      themeColor = '#8b5cf6';
      lightBg = '#f5f3ff';
    } else if (role === 'NV_KHO') {
      themeColor = '#06b6d4';
      lightBg = '#ecfeff';
    } else if (role === 'QUAN_TRI_VIEN') {
      themeColor = '#64748b';
      lightBg = '#f1f5f9';
    }

    // Create container overlay
    const overlay = document.createElement('div');
    overlay.id = 'custom-alert-overlay';
    overlay.style.position = 'fixed';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.right = '0';
    overlay.style.bottom = '0';
    overlay.style.backgroundColor = 'rgba(15, 23, 42, 0.4)';
    overlay.style.backdropFilter = 'blur(4px)';
    overlay.style.zIndex = '999999';
    overlay.style.display = 'flex';
    overlay.style.alignItems = 'center';
    overlay.style.justifyContent = 'center';
    overlay.style.animation = 'customFadeIn 0.2s ease';

    // Create modal box
    const box = document.createElement('div');
    box.style.backgroundColor = '#ffffff';
    box.style.borderRadius = '16px';
    box.style.padding = '30px 24px 24px 24px';
    box.style.width = '90%';
    box.style.maxWidth = '400px';
    box.style.boxShadow = '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)';
    box.style.border = '1px solid #e2e8f0';
    box.style.textAlign = 'center';
    box.style.animation = 'customScaleUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)';

    // Icon header
    const iconWrapper = document.createElement('div');
    iconWrapper.style.width = '64px';
    iconWrapper.style.height = '64px';
    iconWrapper.style.borderRadius = '50%';
    iconWrapper.style.backgroundColor = lightBg;
    iconWrapper.style.color = themeColor;
    iconWrapper.style.display = 'flex';
    iconWrapper.style.alignItems = 'center';
    iconWrapper.style.justifyContent = 'center';
    iconWrapper.style.fontSize = '28px';
    iconWrapper.style.margin = '0 auto 20px auto';
    
    // Choose emoji depending on success/error/warning keywords
    let emoji = '🔔';
    const lowerMsg = message.toString().toLowerCase();
    if (lowerMsg.includes('thành công') || lowerMsg.includes('ok') || lowerMsg.includes('hoàn thành')) {
      emoji = '✅';
    } else if (lowerMsg.includes('lỗi') || lowerMsg.includes('thất bại') || lowerMsg.includes('không đúng') || lowerMsg.includes('không khớp') || lowerMsg.includes('bị khóa') || lowerMsg.includes('không tồn tại')) {
      emoji = '❌';
      iconWrapper.style.backgroundColor = '#fef2f2';
      iconWrapper.style.color = '#ef4444';
    } else if (lowerMsg.includes('hủy') || lowerMsg.includes('chắc chắn')) {
      emoji = '⚠️';
      iconWrapper.style.backgroundColor = '#fffbeb';
      iconWrapper.style.color = '#f59e0b';
    }
    iconWrapper.innerHTML = emoji;

    // Message Text
    const msgEl = document.createElement('div');
    msgEl.style.fontSize = '15px';
    msgEl.style.fontWeight = '600';
    msgEl.style.color = '#1e293b';
    msgEl.style.marginBottom = '24px';
    msgEl.style.lineHeight = '1.6';
    msgEl.style.whiteSpace = 'pre-line';
    msgEl.innerText = message;

    // OK Button
    const btn = document.createElement('button');
    btn.style.width = '100%';
    btn.style.padding = '12px 20px';
    btn.style.backgroundColor = themeColor;
    btn.style.color = '#ffffff';
    btn.style.border = 'none';
    btn.style.borderRadius = '10px';
    btn.style.fontWeight = '700';
    btn.style.fontSize = '14px';
    btn.style.cursor = 'pointer';
    btn.style.transition = 'opacity 0.2s';
    btn.innerText = 'Đồng ý';
    
    btn.onmouseover = function() {
      btn.style.opacity = '0.9';
    };
    btn.onmouseout = function() {
      btn.style.opacity = '1';
    };

    // Close animations
    const closeAlert = function() {
      overlay.style.animation = 'customFadeOut 0.2s ease forwards';
      box.style.animation = 'customScaleDown 0.2s ease forwards';
      setTimeout(() => {
        overlay.remove();
        if (typeof callback === 'function') {
          callback();
        }
      }, 200);
    };

    btn.onclick = closeAlert;

    // Append Styles
    if (!document.getElementById('custom-alert-styles')) {
      const styles = document.createElement('style');
      styles.id = 'custom-alert-styles';
      styles.innerHTML = `
        @keyframes customFadeIn {
          from { opacity: 0; }
          to { opacity: 1; }
        }
        @keyframes customFadeOut {
          from { opacity: 1; }
          to { opacity: 0; }
        }
        @keyframes customScaleUp {
          from { transform: scale(0.9); opacity: 0; }
          to { transform: scale(1); opacity: 1; }
        }
        @keyframes customScaleDown {
          from { transform: scale(1); opacity: 1; }
          to { transform: scale(0.95); opacity: 0; }
        }
      `;
      document.head.appendChild(styles);
    }

    box.appendChild(iconWrapper);
    box.appendChild(msgEl);
    box.appendChild(btn);
    overlay.appendChild(box);
    document.body.appendChild(overlay);

    btn.focus();
  };

  // Custom confirm wrapper
  window.showConfirm = function(message, onConfirm, onCancel) {
    const existing = document.getElementById('custom-confirm-overlay');
    if (existing) {
      existing.remove();
    }
    
    const role = localStorage.getItem('demo_role') || 'KHACH_HANG';
    let themeColor = '#76a89c';
    let lightBg = '#e6f0ed';
    
    if (role === 'NV_KINH_DOANH') {
      themeColor = '#10b981';
      lightBg = '#ecfdf5';
    } else if (role === 'NV_KE_TOAN') {
      themeColor = '#f59e0b';
      lightBg = '#fffbeb';
    } else if (role === 'BAN_QUAN_LY') {
      themeColor = '#8b5cf6';
      lightBg = '#f5f3ff';
    } else if (role === 'NV_KHO') {
      themeColor = '#06b6d4';
      lightBg = '#ecfeff';
    } else if (role === 'QUAN_TRI_VIEN') {
      themeColor = '#64748b';
      lightBg = '#f1f5f9';
    }

    const overlay = document.createElement('div');
    overlay.id = 'custom-confirm-overlay';
    overlay.style.position = 'fixed';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.right = '0';
    overlay.style.bottom = '0';
    overlay.style.backgroundColor = 'rgba(15, 23, 42, 0.4)';
    overlay.style.backdropFilter = 'blur(4px)';
    overlay.style.zIndex = '999999';
    overlay.style.display = 'flex';
    overlay.style.alignItems = 'center';
    overlay.style.justifyContent = 'center';
    overlay.style.animation = 'customFadeIn 0.2s ease';

    const box = document.createElement('div');
    box.style.backgroundColor = '#ffffff';
    box.style.borderRadius = '16px';
    box.style.padding = '30px 24px 24px 24px';
    box.style.width = '90%';
    box.style.maxWidth = '400px';
    box.style.boxShadow = '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)';
    box.style.border = '1px solid #e2e8f0';
    box.style.textAlign = 'center';
    box.style.animation = 'customScaleUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)';

    const iconWrapper = document.createElement('div');
    iconWrapper.style.width = '64px';
    iconWrapper.style.height = '64px';
    iconWrapper.style.borderRadius = '50%';
    iconWrapper.style.backgroundColor = '#fffbeb';
    iconWrapper.style.color = '#f59e0b';
    iconWrapper.style.display = 'flex';
    iconWrapper.style.alignItems = 'center';
    iconWrapper.style.justifyContent = 'center';
    iconWrapper.style.fontSize = '28px';
    iconWrapper.style.margin = '0 auto 20px auto';
    iconWrapper.innerHTML = '⚠️';

    const msgEl = document.createElement('div');
    msgEl.style.fontSize = '15px';
    msgEl.style.fontWeight = '600';
    msgEl.style.color = '#1e293b';
    msgEl.style.marginBottom = '24px';
    msgEl.style.lineHeight = '1.6';
    msgEl.style.whiteSpace = 'pre-line';
    msgEl.innerText = message;

    const btnContainer = document.createElement('div');
    btnContainer.style.display = 'flex';
    btnContainer.style.gap = '12px';
    btnContainer.style.justifyContent = 'center';

    const cancelBtn = document.createElement('button');
    cancelBtn.style.flex = '1';
    cancelBtn.style.padding = '12px 20px';
    cancelBtn.style.backgroundColor = '#f1f5f9';
    cancelBtn.style.color = '#475569';
    cancelBtn.style.border = 'none';
    cancelBtn.style.borderRadius = '10px';
    cancelBtn.style.fontWeight = '700';
    cancelBtn.style.fontSize = '14px';
    cancelBtn.style.cursor = 'pointer';
    cancelBtn.style.transition = 'background-color 0.2s';
    cancelBtn.innerText = 'Hủy';

    cancelBtn.onmouseover = function() {
      cancelBtn.style.backgroundColor = '#e2e8f0';
    };
    cancelBtn.onmouseout = function() {
      cancelBtn.style.backgroundColor = '#f1f5f9';
    };

    const confirmBtn = document.createElement('button');
    confirmBtn.style.flex = '1';
    confirmBtn.style.padding = '12px 20px';
    confirmBtn.style.backgroundColor = themeColor;
    confirmBtn.style.color = '#ffffff';
    confirmBtn.style.border = 'none';
    confirmBtn.style.borderRadius = '10px';
    confirmBtn.style.fontWeight = '700';
    confirmBtn.style.fontSize = '14px';
    confirmBtn.style.cursor = 'pointer';
    confirmBtn.style.transition = 'opacity 0.2s';
    confirmBtn.innerText = 'Đồng ý';

    confirmBtn.onmouseover = function() {
      confirmBtn.style.opacity = '0.9';
    };
    confirmBtn.onmouseout = function() {
      confirmBtn.style.opacity = '1';
    };

    const closeConfirm = function() {
      overlay.style.animation = 'customFadeOut 0.2s ease forwards';
      box.style.animation = 'customScaleDown 0.2s ease forwards';
      setTimeout(() => {
        overlay.remove();
      }, 200);
    };

    cancelBtn.onclick = function() {
      closeConfirm();
      if (typeof onCancel === 'function') {
        onCancel();
      }
    };

    confirmBtn.onclick = function() {
      closeConfirm();
      if (typeof onConfirm === 'function') {
        onConfirm();
      }
    };

    btnContainer.appendChild(cancelBtn);
    btnContainer.appendChild(confirmBtn);

    box.appendChild(iconWrapper);
    box.appendChild(msgEl);
    box.appendChild(btnContainer);
    overlay.appendChild(box);
    document.body.appendChild(overlay);

    confirmBtn.focus();
  };
})();
