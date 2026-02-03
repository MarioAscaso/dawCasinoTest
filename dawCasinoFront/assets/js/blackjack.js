/**
 * BLACKJACK.JS - Versión Final (Event Listeners & Clean Code)
 */

let currentGameId = null;
let user = null;

// Variables de Sesión
let sessionStartBalance = 0;
let sessionStartTime = null;
let sessionTimerInterval = null;
let sessionLimits = {
    timeMinutes: 60,
    maxLoss: 500
};

const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// --- INICIALIZACIÓN ---
document.addEventListener('DOMContentLoaded', async () => {
    // 1. Configurar Event Listeners (Sustituye a los onclick)
    setupEventListeners();

    // 2. Validar Login
    const userStored = localStorage.getItem('user');
    if (!userStored) {
        window.location.href = 'login.html';
        return;
    }
    user = JSON.parse(userStored);
    
    // 3. Sincronizar datos reales del servidor
    await syncUserData();

    // 4. Aplicar preferencias visuales (Avatar/Bandera)
    applyVisualPreferences();
});

// --- GESTIÓN DE EVENT LISTENERS ---
function setupEventListeners() {
    // Botones de la Sidebar / Cabecera
    const btnEndSession = document.getElementById('btn-end-session');
    const btnDeposit = document.getElementById('btn-deposit');

    if(btnEndSession) btnEndSession.addEventListener('click', endSession);
    if(btnDeposit) btnDeposit.addEventListener('click', depositMoney);

    // Controles de Apuesta (Fichas)
    const chipBtns = document.querySelectorAll('.chip-btn');
    chipBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const amount = parseFloat(btn.getAttribute('data-amount'));
            addChip(amount);
        });
    });

    const btnClearBet = document.getElementById('btn-clear-bet');
    if(btnClearBet) btnClearBet.addEventListener('click', clearBet);

    // Acciones del Juego
    const btnStartGame = document.getElementById('btn-start-game');
    const btnHit = document.getElementById('btn-hit');
    const btnStand = document.getElementById('btn-stand');

    if(btnStartGame) btnStartGame.addEventListener('click', startGame);
    if(btnHit) btnHit.addEventListener('click', () => playAction("HIT"));
    if(btnStand) btnStand.addEventListener('click', () => playAction("STAND"));

    // Modales (Botones internos)
    const btnConfirmSession = document.getElementById('btn-confirm-session');
    const btnBackLobby = document.getElementById('btn-back-lobby');
    const btnBackLobby2 = document.getElementById('btn-back-lobby-2'); // Del resumen final
    const btnReplay = document.getElementById('btn-replay');

    if(btnConfirmSession) btnConfirmSession.addEventListener('click', startSessionConfig);
    
    const goLobby = () => window.location.href = 'index.html';
    if(btnBackLobby) btnBackLobby.addEventListener('click', goLobby);
    if(btnBackLobby2) btnBackLobby2.addEventListener('click', goLobby);
    
    if(btnReplay) btnReplay.addEventListener('click', () => location.reload());
}


// --- SINCRONIZACIÓN Y DATOS ---

async function syncUserData() {
    try {
        const response = await fetch(`${CONFIG.API_URL}/users/${user.id}`);
        
        if (response.ok) {
            const freshUser = await response.json();
            console.log(`Sincronizado: Saldo local ${user.balance} -> Servidor ${freshUser.balance}`);
            
            user = freshUser;
            localStorage.setItem('user', JSON.stringify(user));
            updateBalanceDisplay();
        }
    } catch (error) {
        console.warn("Modo Offline: No se pudo sincronizar con el servidor", error);
    }
}

// --- LÓGICA DE FICHAS ---
function addChip(amount) {
    const input = document.getElementById('bet-amount');
    let currentVal = parseFloat(input.value) || 0;
    const newVal = currentVal + amount;
    
    if (newVal > user.balance) {
        alert("¡No tienes tantas fichas! Ingresa dinero primero.");
        return;
    }
    input.value = newVal;
}

function clearBet() {
    document.getElementById('bet-amount').value = 0;
}


// --- CONFIGURACIÓN DE SESIÓN (LÍMITES) ---

async function startSessionConfig() {
    const timeLimit = parseInt(document.getElementById('limit-time').value) || 60;
    const lossLimit = parseFloat(document.getElementById('limit-loss').value) || 500;
    
    try {
        // Guardar límites en backend
        const response = await fetch(`${CONFIG.API_URL}/users/profile`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: user.id,
                avatar: user.avatar,      
                avatarType: user.avatarType,
                dailyLossLimit: lossLimit,
                sessionTimeLimit: timeLimit
            })
        });

        if (!response.ok) throw new Error("Error al guardar perfil");

        const updatedUser = await response.json();
        user = updatedUser;
        localStorage.setItem('user', JSON.stringify(user));

        // Configurar variables locales
        sessionLimits.timeMinutes = timeLimit;
        sessionLimits.maxLoss = lossLimit;
        sessionStartBalance = user.balance;
        sessionStartTime = new Date();

        document.getElementById('setup-modal').classList.remove('active');
        updateBalanceDisplay();
        loadHistory();
        startTimer();

    } catch (error) {
        console.error(error);
        alert("Hubo un problema guardando tu configuración, pero puedes jugar igual.");
        document.getElementById('setup-modal').classList.remove('active');
        
        sessionLimits.timeMinutes = timeLimit;
        sessionLimits.maxLoss = lossLimit;
        sessionStartBalance = user.balance;
        sessionStartTime = new Date();
        startTimer();
    }
}

function applyVisualPreferences() {
    const avatarEl = document.getElementById('header-avatar');
    
    // Comprobar si es bandera
    const isFlag = user.avatarType && user.avatarType.length < 10 && user.avatarType !== "IMAGE";
    
    let flagSpan = document.getElementById('header-flag-emoji');
    if(!flagSpan) {
        flagSpan = document.createElement('span');
        flagSpan.id = 'header-flag-emoji';
        flagSpan.className = 'flag-icon';
        document.querySelector('.user-profile-display').prepend(flagSpan);
    }
    
    if (isFlag) {
        flagSpan.innerText = user.avatarType;
        flagSpan.classList.remove('hidden');
        avatarEl.classList.add('hidden');
    } else {
        flagSpan.classList.add('hidden');
        avatarEl.src = user.avatar || `https://api.dicebear.com/9.x/avataaars/svg?seed=${user.username}`;
        avatarEl.classList.remove('hidden');
    }
    document.getElementById('username-display').innerText = user.username;
}


// --- JUEGO RESPONSABLE (TIMER & LÍMITES) ---

function startTimer() {
    sessionTimerInterval = setInterval(() => {
        const now = new Date();
        const diffMs = now - sessionStartTime;
        const diffMins = Math.floor(diffMs / 60000);
        const diffSecs = Math.floor((diffMs % 60000) / 1000);

        const str = `${diffMins.toString().padStart(2, '0')}:${diffSecs.toString().padStart(2, '0')}`;
        const timerEl = document.getElementById('session-timer');
        if(timerEl) timerEl.innerText = str;

        if (diffMins >= sessionLimits.timeMinutes) {
            clearInterval(sessionTimerInterval);
            alert("⚠️ ¡TIEMPO AGOTADO! ⚠️\nPor tu seguridad, la sesión ha finalizado.");
            endSession();
        }
    }, 1000);
}

function checkResponsibleGamingLimits(betAmount) {
    const currentLoss = sessionStartBalance - user.balance;
    
    if (currentLoss >= sessionLimits.maxLoss) {
        alert(`🛑 LÍMITE DE PÉRDIDAS ALCANZADO 🛑\nHas alcanzado tu límite de ${sessionLimits.maxLoss}€. Es hora de descansar.`);
        endSession();
        return false;
    }
    
    if ((currentLoss + betAmount) > sessionLimits.maxLoss) {
        alert(`⚠️ Esta apuesta superaría tu límite de pérdidas (${sessionLimits.maxLoss}€).`);
        return false;
    }
    return true;
}


// --- CORE DEL JUEGO ---

async function startGame() {
    const betInput = document.getElementById('bet-amount');
    const bet = parseFloat(betInput.value);
    const currentBalance = parseFloat(user.balance);

    if (isNaN(bet) || bet < 10) { alert("La apuesta mínima son 10€"); return; }
    if (bet > currentBalance) { alert("Saldo insuficiente. Usa el botón (+) para ingresar."); return; }

    if (!checkResponsibleGamingLimits(bet)) return;

    try {
        const response = await fetch(`${CONFIG.API_URL}/games/blackjack/start`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId: user.id, betAmount: bet })
        });

        await handleErrors(response);
        const game = await response.json();
        currentGameId = game.id;

        user.balance = currentBalance - bet; 
        updateBalanceDisplay();
        hideStatus();
        
        await animateInitialDeal(game);
        
        if (game.status !== "PLAYING") {
             setTimeout(() => finishGame(game), 500);
        } else {
             toggleControls(true);
        }

    } catch (error) {
        alert("Error al empezar: " + error.message);
    }
}

async function playAction(action) {
    try {
        const response = await fetch(`${CONFIG.API_URL}/games/blackjack/play`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ gameId: currentGameId, action: action })
        });

        await handleErrors(response);
        const game = await response.json();
        
        if (action === "HIT") {
            renderGame(game);
            if (game.status !== "PLAYING") finishGame(game);
        } else if (action === "STAND") {
            await animateDealerTurn(game);
            finishGame(game);
        }

    } catch (error) {
        console.error(error);
    }
}


// --- RENDERIZADO Y ANIMACIONES ---

async function animateInitialDeal(game) {
    const pContainer = document.getElementById('player-cards');
    const dContainer = document.getElementById('dealer-cards');
    pContainer.innerHTML = '';
    dContainer.innerHTML = '';
    document.getElementById('player-score').innerText = '0';
    document.getElementById('dealer-score').innerText = '0';

    appendCard(pContainer, game.playerHand.cards[0]);
    await sleep(400); 
    
    appendCard(dContainer, game.dealerHand.cards[0]);
    document.getElementById('dealer-score').innerText = calculateSingleCardValue(game.dealerHand.cards[0]);
    await sleep(400);

    appendCard(pContainer, game.playerHand.cards[1]);
    document.getElementById('player-score').innerText = game.playerHand.score;
    await sleep(400);

    appendCardHidden(dContainer);
}

async function animateDealerTurn(game) {
    const dContainer = document.getElementById('dealer-cards');
    const dealerCards = game.dealerHand.cards;

    dContainer.innerHTML = '';
    appendCard(dContainer, dealerCards[0]); 
    appendCard(dContainer, dealerCards[1]); 
    
    let currentScore = calculatePartialScore([dealerCards[0], dealerCards[1]]);
    document.getElementById('dealer-score').innerText = currentScore;
    
    await sleep(800);

    for (let i = 2; i < dealerCards.length; i++) {
        appendCard(dContainer, dealerCards[i]);
        currentScore = calculatePartialScore(dealerCards.slice(0, i + 1));
        document.getElementById('dealer-score').innerText = currentScore;
        await sleep(800);
    }

    document.getElementById('dealer-score').innerText = game.dealerHand.score;
}

function renderGame(game) {
    const isPlaying = game.status === "PLAYING";

    drawHand('player-cards', game.playerHand.cards);
    document.getElementById('player-score').innerText = game.playerHand.score || '?';

    if (isPlaying) {
        drawHand('dealer-cards', game.dealerHand.cards, true);
        if (game.dealerHand.cards.length > 0) {
             const visibleScore = calculateSingleCardValue(game.dealerHand.cards[0]);
             document.getElementById('dealer-score').innerText = visibleScore + "+ ?";
        }
    } else {
        drawHand('dealer-cards', game.dealerHand.cards, false);
        document.getElementById('dealer-score').innerText = game.dealerHand.score;
    }
}

function drawHand(elementId, cards, hideSecondCard = false) {
    const container = document.getElementById(elementId);
    container.innerHTML = '';
    
    cards.forEach((card, index) => {
        if (hideSecondCard && index === 1) {
            appendCardHidden(container);
        } else {
            appendCard(container, card);
        }
    });
}

function appendCard(container, card) {
    const div = document.createElement('div');
    const isRed = card.suit === 'HEARTS' || card.suit === 'DIAMONDS';
    const symbol = getSymbol(card.suit);
    const rankDisplay = getRankDisplay(card.rank);
    
    div.className = `card ${isRed ? 'red' : 'black'} card-appear`;
    div.innerHTML = `${rankDisplay}${symbol}`; 
    container.appendChild(div);
}

function appendCardHidden(container) {
    const div = document.createElement('div');
    div.className = `card back card-appear`;
    container.appendChild(div);
}


// --- FIN DEL JUEGO Y UTILIDADES ---

function finishGame(game) {
    toggleControls(false); 
    
    if (game.status === "PLAYER_WINS") {
        user.balance += (game.betAmount * 2);
    } else if (game.status === "DRAW") {
        user.balance += game.betAmount;
    }
    
    localStorage.setItem('user', JSON.stringify(user));
    updateBalanceDisplay();
    
    setTimeout(() => {
        const msg = getEndMessage(game);
        showStatus(msg);
        setTimeout(loadHistory, 500); 
    }, 1500);
}

function getEndMessage(game) {
    const pScore = game.playerHand.score;
    const dScore = game.dealerHand.score;

    if (game.status === "PLAYER_WINS") {
        if (dScore > 21) return "🏦 LA BANCA SE HA PASADO 💥\n¡GANAS TÚ!";
        if (game.playerHand.blackJack) return "✨ ¡BLACKJACK! ✨\n¡PAGO 2x!";
        return `🏆 ¡GANAS! 🏆\n(${pScore} vs ${dScore})`;
    } 
    
    if (game.status === "DEALER_WINS") {
        if (pScore > 21) return "💀 TE HAS PASADO 💀\nHas perdido.";
        if (game.dealerHand.blackJack) return "♠️ LA BANCA TIENE BLACKJACK ♠️";
        return `📉 LA BANCA GANA 📉\n(${dScore} vs ${pScore})`;
    }

    return "🤝 EMPATE (PUSH) 🤝\nRecuperas tu apuesta.";
}

function endSession() {
    clearInterval(sessionTimerInterval); 

    const finalBalance = user.balance;
    const netResult = finalBalance - sessionStartBalance;
    const timePlayed = document.getElementById('session-timer').innerText;

    document.getElementById('summary-time').innerText = timePlayed;
    document.getElementById('summary-start').innerText = sessionStartBalance.toFixed(2);
    document.getElementById('summary-end').innerText = finalBalance.toFixed(2);
    
    const resultEl = document.getElementById('summary-result');
    if (netResult >= 0) {
        resultEl.className = 'win';
        resultEl.innerText = `Beneficio: +${netResult.toFixed(2)} € 📈`;
    } else {
        resultEl.className = 'lose';
        resultEl.innerText = `Pérdida: ${netResult.toFixed(2)} € 📉`;
    }

    document.getElementById('summary-modal').classList.add('active');
}

async function depositMoney() {
    const amountStr = prompt("💰 INGRESO RÁPIDO 💰\n\n¿Cuánto quieres ingresar?");
    if (!amountStr) return; 

    const amount = parseFloat(amountStr);
    if (isNaN(amount) || amount <= 0) {
        alert("Por favor, introduce una cantidad válida.");
        return;
    }

    try {
        const response = await fetch(`${CONFIG.API_URL}/wallet/deposit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId: user.id, amount: amount })
        });

        await handleErrors(response);
        const updatedUser = await response.json();

        user.balance = updatedUser.balance;
        localStorage.setItem('user', JSON.stringify(user));
        updateBalanceDisplay();
        
        sessionStartBalance += amount; 

    } catch (error) {
        alert("Error al ingresar: " + error.message);
    }
}

async function loadHistory() {
    const container = document.getElementById('history-list');
    try {
        const response = await fetch(`${CONFIG.API_URL}/games/history/${user.id}`);
        
        if (!response.ok) return;
        
        const history = await response.json();
        container.innerHTML = ''; 

        if (history.length === 0) {
            container.innerHTML = '<p style="color:#aaa; font-style:italic; padding:10px;">No hay partidas recientes.</p>';
            return;
        }

        history.forEach(game => {
            const div = document.createElement('div');
            let resultClass = 'draw';
            let symbol = '=';
            
            if (game.status === 'PLAYER_WINS') {
                resultClass = 'win';
                symbol = '+';
            } else if (game.status === 'DEALER_WINS') {
                resultClass = 'lose';
                symbol = '-';
            }

            const dateObj = new Date(game.date);
            const dateStr = dateObj.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});

            div.className = `history-item ${resultClass}`;
            div.innerHTML = `
                <div style="display:flex; justify-content:space-between;">
                    <strong>${game.status === 'PLAYER_WINS' ? 'VICTORIA' : (game.status === 'DEALER_WINS' ? 'DERROTA' : 'EMPATE')}</strong>
                    <span class="history-amount">${symbol}${game.betAmount}€</span>
                </div>
                <span class="history-date">${dateStr}</span>
            `;
            container.appendChild(div);
        });

    } catch (e) {
        container.innerHTML = `<p style="color:#e74c3c; padding:10px; font-size:0.8rem;">⚠️ Error de conexión</p>`;
    }
}


// --- HELPERS UI ---

function getRankDisplay(rank) {
    const rankMap = {
        'TWO': '2', 'THREE': '3', 'FOUR': '4', 'FIVE': '5',
        'SIX': '6', 'SEVEN': '7', 'EIGHT': '8', 'NINE': '9',
        'TEN': '10', 'JACK': 'J', 'QUEEN': 'Q', 'KING': 'K', 'ACE': 'A'
    };
    return rankMap[rank] || rank.substring(0, 1);
}

function getSymbol(suit) {
    if (suit === 'HEARTS') return '♥';
    if (suit === 'DIAMONDS') return '♦';
    if (suit === 'SPADES') return '♠';
    return '♣';
}

function calculateSingleCardValue(card) {
    if (['JACK', 'QUEEN', 'KING', 'TEN'].includes(card.rank)) return 10;
    if (card.rank === 'ACE') return 11;
    const rankMap = {'TWO':2, 'THREE':3, 'FOUR':4, 'FIVE':5, 'SIX':6, 'SEVEN':7, 'EIGHT':8, 'NINE':9};
    return rankMap[card.rank] || 0;
}

function calculatePartialScore(cards) {
    let score = 0;
    let aces = 0;
    cards.forEach(c => {
        score += calculateSingleCardValue(c);
        if (c.rank === 'ACE') aces++;
    });
    while (score > 21 && aces > 0) {
        score -= 10;
        aces--;
    }
    return score;
}

function toggleControls(isPlaying) {
    if (isPlaying) {
        document.getElementById('bet-panel').classList.add('hidden');
        document.getElementById('action-panel').classList.remove('hidden');
    } else {
        document.getElementById('bet-panel').classList.remove('hidden');
        document.getElementById('action-panel').classList.add('hidden');
    }
}

function showStatus(text) {
    const el = document.getElementById('status-message');
    el.innerText = text;
    el.classList.remove('hidden');
}

function hideStatus() {
    document.getElementById('status-message').classList.add('hidden');
}

function updateBalanceDisplay() {
    const el = document.getElementById('balance-display');
    if (el) el.innerText = user.balance;
}