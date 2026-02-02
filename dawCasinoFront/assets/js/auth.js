// --- LÓGICA DE REGISTRO ---
const registerForm = document.getElementById('registerForm');

if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const messageBox = setupMessageBox();

        // 1. Obtener tipo visual seleccionado (Imagen o Bandera)
        const type = document.querySelector('input[name="visualType"]:checked').value;
        const avatarUrl = document.getElementById('reg-avatar-preview').src;
        const flagCode = document.getElementById('reg-flag-select').value;

        // 2. Preparar datos para el backend
        // Si elige IMAGE -> avatar=URL, avatarType="IMAGE"
        // Si elige FLAG  -> avatar=null, avatarType="🇪🇸" (código bandera)
        
        const finalAvatar = (type === 'IMAGE') ? avatarUrl : null;
        const finalType = (type === 'FLAG') ? flagCode : "IMAGE";

        const data = {
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            avatar: finalAvatar,
            avatarType: finalType
        };

        try {
            const response = await fetch(`${CONFIG.API_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            await handleErrors(response);

            showMessage(messageBox, "¡Cuenta creada! Redirigiendo...", "success");
            setTimeout(() => window.location.href = 'login.html', 2000);

        } catch (error) {
            showMessage(messageBox, error.message, "error");
        }
    });
}

// --- LÓGICA DE LOGIN ---
const loginForm = document.getElementById('loginForm');

if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const messageBox = setupMessageBox();

        const data = {
            username: document.getElementById('username').value,
            password: document.getElementById('password').value
        };

        try {
            const response = await fetch(`${CONFIG.API_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            await handleErrors(response);
            
            const user = await response.json();
            
            // Guardamos usuario en LocalStorage
            localStorage.setItem('user', JSON.stringify(user));

            showMessage(messageBox, "¡Bienvenido! Entrando...", "success");
            setTimeout(() => window.location.href = 'index.html', 1500);

        } catch (error) {
            showMessage(messageBox, "Credenciales incorrectas", "error");
        }
    });
}

// --- FUNCIONES AUXILIARES ---
function setupMessageBox() {
    const box = document.getElementById('message-box');
    box.style.display = 'none';
    box.className = '';
    return box;
}

function showMessage(element, text, type) {
    element.innerText = text;
    element.classList.add(type);
    element.style.display = 'block';
}