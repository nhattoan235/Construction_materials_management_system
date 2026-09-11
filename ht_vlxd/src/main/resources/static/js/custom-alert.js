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

// --- AI Assistant Floating Widget Injection ---
(function() {
  // Styles for the AI Assistant
  const css = `
    /* Floating Button */
    .ai-chat-btn {
      position: fixed;
      bottom: 24px;
      right: 24px;
      width: 56px;
      height: 56px;
      border-radius: 50%;
      background: linear-gradient(135deg, #3b6d63 0%, #2a4e47 100%);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      box-shadow: 0 6px 20px rgba(59, 109, 99, 0.4);
      z-index: 99999;
      transition: transform 0.2s ease, box-shadow 0.2s ease;
      font-size: 24px;
    }
    .ai-chat-btn:hover {
      transform: scale(1.1) rotate(5deg);
      box-shadow: 0 8px 24px rgba(59, 109, 99, 0.6);
    }
    .ai-chat-btn:active {
      transform: scale(0.95);
    }
    
    /* Pulse Animation for Button */
    .ai-chat-btn::after {
      content: '';
      position: absolute;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      border: 2px solid #3b6d63;
      opacity: 0;
      animation: aiPulse 2s infinite;
      box-sizing: border-box;
    }
    @keyframes aiPulse {
      0% { transform: scale(1); opacity: 0.5; }
      100% { transform: scale(1.4); opacity: 0; }
    }

    /* Chat Window */
    .ai-chat-window {
      position: fixed;
      bottom: 96px;
      right: 24px;
      width: 380px;
      height: 500px;
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(226, 232, 240, 0.8);
      border-radius: 20px;
      box-shadow: 0 12px 30px rgba(15, 23, 42, 0.15);
      z-index: 99999;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      transform: translateY(20px) scale(0.95);
      opacity: 0;
      pointer-events: none;
      transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.3s ease;
    }
    .ai-chat-window.open {
      transform: translateY(0) scale(1);
      opacity: 1;
      pointer-events: auto;
    }
    
    /* Responsive Chat Window */
    @media (max-width: 480px) {
      .ai-chat-window {
        width: calc(100% - 32px);
        height: 80vh;
        bottom: 90px;
        right: 16px;
      }
    }

    /* Header */
    .ai-chat-header {
      background: linear-gradient(135deg, #3b6d63 0%, #2a4e47 100%);
      color: white;
      padding: 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid rgba(0,0,0,0.05);
    }
    .ai-chat-title-group {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .ai-chat-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background-color: rgba(255,255,255,0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
    }
    .ai-chat-title {
      font-weight: 700;
      font-size: 15px;
      line-height: 1.2;
    }
    .ai-chat-subtitle {
      font-size: 11px;
      opacity: 0.8;
      margin-top: 2px;
    }
    .ai-chat-close {
      cursor: pointer;
      font-size: 20px;
      opacity: 0.8;
      transition: opacity 0.2s;
    }
    .ai-chat-close:hover {
      opacity: 1;
    }

    /* Message Body */
    .ai-chat-body {
      flex: 1;
      padding: 16px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 12px;
      background-color: #f8fafc;
    }
    
    /* Message Bubbles */
    .ai-msg {
      max-width: 80%;
      padding: 10px 14px;
      border-radius: 16px;
      font-size: 13.5px;
      line-height: 1.5;
      word-wrap: break-word;
      animation: aiMsgFadeIn 0.25s ease-out;
    }
    @keyframes aiMsgFadeIn {
      from { opacity: 0; transform: translateY(10px); }
      to { opacity: 1; transform: translateY(0); }
    }
    
    .ai-msg.user {
      align-self: flex-end;
      background-color: #3b6d63;
      color: white;
      border-bottom-right-radius: 4px;
    }
    .ai-msg.bot {
      align-self: flex-start;
      background-color: white;
      color: #1e293b;
      border: 1px solid #e2e8f0;
      border-bottom-left-radius: 4px;
    }
    
    /* Typing Indicator */
    .ai-typing-indicator {
      display: flex;
      gap: 4px;
      padding: 12px 16px;
      align-self: flex-start;
      background-color: white;
      border: 1px solid #e2e8f0;
      border-radius: 16px;
      border-bottom-left-radius: 4px;
      align-items: center;
    }
    .ai-dot {
      width: 6px;
      height: 6px;
      background-color: #94a3b8;
      border-radius: 50%;
      animation: aiDotBounce 1.4s infinite ease-in-out both;
    }
    .ai-dot:nth-child(1) { animation-delay: -0.32s; }
    .ai-dot:nth-child(2) { animation-delay: -0.16s; }
    @keyframes aiDotBounce {
      0%, 80%, 100% { transform: scale(0); }
      40% { transform: scale(1.0); }
    }

    /* Input Footer */
    .ai-chat-footer {
      padding: 12px;
      background-color: white;
      border-top: 1px solid #e2e8f0;
      display: flex;
      gap: 8px;
    }
    .ai-chat-input {
      flex: 1;
      border: 1px solid #cbd5e1;
      border-radius: 10px;
      padding: 8px 12px;
      font-size: 13.5px;
      outline: none;
      transition: border-color 0.2s;
    }
    .ai-chat-input:focus {
      border-color: #3b6d63;
    }
    .ai-chat-send-btn {
      background-color: #3b6d63;
      color: white;
      border: none;
      border-radius: 10px;
      padding: 8px 14px;
      cursor: pointer;
      font-weight: 600;
      font-size: 13.5px;
      transition: background-color 0.2s;
    }
    .ai-chat-send-btn:hover {
      background-color: #2a4e47;
    }
  `;

  // Inject CSS
  const styleEl = document.createElement('style');
  styleEl.textContent = css;
  document.head.appendChild(styleEl);

  // Initialize UI on DOMContentLoaded
  function initAiWidget() {
    // Prevent duplicate widgets
    if (document.getElementById('ai-assistant-widget')) return;

    const container = document.createElement('div');
    container.id = 'ai-assistant-widget';

    // HTML Structure
    container.innerHTML = `
      <!-- Floating Action Button -->
      <div class="ai-chat-btn" id="aiChatBtn" title="Trợ lý AI">
        💬
      </div>

      <!-- Chat Drawer/Window -->
      <div class="ai-chat-window" id="aiChatWindow">
        <!-- Header -->
        <div class="ai-chat-header">
          <div class="ai-chat-title-group">
            <div class="ai-chat-avatar">🤖</div>
            <div>
              <div class="ai-chat-title">Trợ lý ảo CMC</div>
              <div class="ai-chat-subtitle">Trực tuyến - Tư vấn vật tư 24/7</div>
            </div>
          </div>
          <div class="ai-chat-close" id="aiChatClose">&times;</div>
        </div>

        <!-- Chat Messages -->
        <div class="ai-chat-body" id="aiChatBody">
          <!-- Messages will go here -->
        </div>

        <!-- Input Area -->
        <div class="ai-chat-footer">
          <input type="text" class="ai-chat-input" id="aiChatInput" placeholder="Nhập câu hỏi tại đây..." autocomplete="off">
          <button class="ai-chat-send-btn" id="aiChatSendBtn">Gửi</button>
        </div>
      </div>
    `;

    document.body.appendChild(container);

    const btn = document.getElementById('aiChatBtn');
    const win = document.getElementById('aiChatWindow');
    const close = document.getElementById('aiChatClose');
    const input = document.getElementById('aiChatInput');
    const sendBtn = document.getElementById('aiChatSendBtn');
    const body = document.getElementById('aiChatBody');

    // --- Drag and Drop Logic for Floating Button ---
    let isDragging = false;
    let dragStartX = 0;
    let dragStartY = 0;
    let buttonStartX = 0;
    let buttonStartY = 0;
    let hasMoved = false;

    btn.addEventListener('mousedown', dragStart);
    btn.addEventListener('touchstart', dragStart, { passive: true });

    function dragStart(e) {
      const rect = btn.getBoundingClientRect();
      buttonStartX = rect.left;
      buttonStartY = rect.top;

      dragStartX = e.type === 'touchstart' ? e.touches[0].clientX : e.clientX;
      dragStartY = e.type === 'touchstart' ? e.touches[0].clientY : e.clientY;

      isDragging = true;
      hasMoved = false;

      // Temporary listeners for moves and end
      document.addEventListener('mousemove', dragMove);
      document.addEventListener('touchmove', dragMove, { passive: false });
      document.addEventListener('mouseup', dragEnd);
      document.addEventListener('touchend', dragEnd);
    }

    function dragMove(e) {
      if (!isDragging) return;

      // Prevent scrolling on touch screens
      if (e.cancelable) {
        e.preventDefault();
      }

      const clientX = e.type === 'touchmove' ? e.touches[0].clientX : e.clientX;
      const clientY = e.type === 'touchmove' ? e.touches[0].clientY : e.clientY;

      const deltaX = clientX - dragStartX;
      const deltaY = clientY - dragStartY;

      // Check if user has moved the button enough to call it a drag
      if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
        hasMoved = true;
      }

      let newLeft = buttonStartX + deltaX;
      let newTop = buttonStartY + deltaY;

      // Boundary checks (keep inside viewport)
      const viewportWidth = window.innerWidth;
      const viewportHeight = window.innerHeight;
      const btnSize = 56;

      if (newLeft < 10) newLeft = 10;
      if (newLeft > viewportWidth - btnSize - 10) newLeft = viewportWidth - btnSize - 10;
      if (newTop < 10) newTop = 10;
      if (newTop > viewportHeight - btnSize - 10) newTop = viewportHeight - btnSize - 10;

      btn.style.left = newLeft + 'px';
      btn.style.top = newTop + 'px';
      btn.style.bottom = 'auto';
      btn.style.right = 'auto';
      
      // If chat is open, reposition it on move
      if (win.classList.contains('open')) {
        alignChatWindow(newLeft, newTop);
      }
    }

    function dragEnd() {
      if (!isDragging) return;
      isDragging = false;
      document.removeEventListener('mousemove', dragMove);
      document.removeEventListener('touchmove', dragMove);
      document.removeEventListener('mouseup', dragEnd);
      document.removeEventListener('touchend', dragEnd);
    }

    function alignChatWindow(btnLeft, btnTop) {
      const viewportWidth = window.innerWidth;
      const winWidth = 380;
      const winHeight = 500;

      let winLeft = btnLeft - winWidth + 56;
      let winTop = btnTop - winHeight - 16;

      // Keep chat window inside viewport
      if (winLeft < 10) winLeft = 10;
      if (winLeft + winWidth > viewportWidth - 10) winLeft = viewportWidth - winWidth - 10;
      if (winTop < 10) {
        // Open downwards if not enough space on top
        winTop = btnTop + 56 + 16;
      }

      win.style.left = winLeft + 'px';
      win.style.top = winTop + 'px';
      win.style.bottom = 'auto';
      win.style.right = 'auto';
    }

    // Toggle Chat Window
    btn.addEventListener('click', function(e) {
      e.stopPropagation();
      if (hasMoved) return; // Ignore clicks if dragging just ended

      win.classList.toggle('open');
      if (win.classList.contains('open')) {
        input.focus();
        
        // Position chat window relative to current button coordinates
        const rect = btn.getBoundingClientRect();
        alignChatWindow(rect.left, rect.top);
        
        // Load messages from session storage
        loadChatHistory();
      }
    });

    close.addEventListener('click', function(e) {
      e.stopPropagation();
      win.classList.remove('open');
    });

    // Close window if click outside
    document.addEventListener('click', function(e) {
      if (win.classList.contains('open') && !win.contains(e.target) && e.target !== btn) {
        win.classList.remove('open');
      }
    });

    win.addEventListener('click', function(e) {
      e.stopPropagation();
    });

    // Handle message sending
    function sendMessage() {
      const message = input.value.trim();
      if (!message) return;

      appendMessage('user', message);
      input.value = '';
      saveMessageToHistory('user', message);

      // Add typing indicator
      const typingEl = addTypingIndicator();
      body.scrollTop = body.scrollHeight;

      // Call API
      fetch('/api/ai/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: message })
      })
      .then(res => {
        typingEl.remove();
        if (res.ok) {
          return res.json();
        }
        throw new Error('API Error');
      })
      .then(data => {
        appendMessage('bot', data.reply);
        saveMessageToHistory('bot', data.reply);
      })
      .catch(err => {
        console.error(err);
        typingEl.remove();
        const errorMsg = 'Rất tiếc, tôi đang gặp khó khăn khi kết nối. Bạn hãy thử kiểm tra lại cấu hình API key nhé!';
        appendMessage('bot', errorMsg);
        saveMessageToHistory('bot', errorMsg);
      });
    }

    sendBtn.addEventListener('click', sendMessage);
    input.addEventListener('keypress', function(e) {
      if (e.key === 'Enter') {
        sendMessage();
      }
    });

    function appendMessage(sender, text) {
      const msg = document.createElement('div');
      msg.className = `ai-msg ${sender}`;
      msg.innerText = text;
      body.appendChild(msg);
      body.scrollTop = body.scrollHeight;
    }

    function addTypingIndicator() {
      const indicator = document.createElement('div');
      indicator.className = 'ai-typing-indicator';
      indicator.innerHTML = `
        <div class="ai-dot"></div>
        <div class="ai-dot"></div>
        <div class="ai-dot"></div>
      `;
      body.appendChild(indicator);
      return indicator;
    }

    // Chat History Management (Session-wide context retention)
    function saveMessageToHistory(sender, text) {
      let history = JSON.parse(sessionStorage.getItem('ai_chat_history') || '[]');
      history.push({ sender: sender, text: text });
      sessionStorage.setItem('ai_chat_history', JSON.stringify(history));
    }

    function loadChatHistory() {
      body.innerHTML = '';
      let history = JSON.parse(sessionStorage.getItem('ai_chat_history') || '[]');
      if (history.length === 0) {
        // Welcome message
        const welcome = 'Xin chào! Tôi là Trợ lý ảo của Sài Gòn CMC. Tôi có thể giúp gì cho bạn trong việc chọn mua hay tính toán khối lượng vật liệu xây dựng?';
        appendMessage('bot', welcome);
        saveMessageToHistory('bot', welcome);
      } else {
        history.forEach(msg => {
          appendMessage(msg.sender, msg.text);
        });
      }
    }
  }

  // Hook into DOM Ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAiWidget);
  } else {
    initAiWidget();
  }
})();
