const suggestedGamesData = [
    { title: "Dark Souls 3", motivation: "You showed interest in Souls-Like games", img: "./src/ds3.png" },
    { title: "Sekiro: Shadows Die Twice", motivation: "8 of your friends played this game", img: "./src/sekiro.jpg" },
    { title: "Bloodborne", motivation: "You liked SoulsLike, 3 friends of yours loved it", img: "./src/blood.png" }
];
const suggestedGamersData = [
    {userName: "Aldo B.", motivation: "You share 2 games with this user", img: "./src/avatar.jpg"},
    {userName: "Giovanni S.", motivation: "You share the passion for 'Souls-Like' games with this user", img: "./src/giovanni.jpg"},
    {userName: "Giacomo P.", motivation: "You share 1 friend in common with this user", img: "./src/poretti.png"},
    {userName: "Pietro P.", motivation: "You like the same snacks", img: "./src/pacciani.jpg"},
    {userName: "Giancarlo L.", motivation: "You like the same snacks", img: "./src/gianc.jpg"},
    {userName: "Mario V.", motivation: "You like the same snacks", img: "./src/vanni.jpg"},
];

async function getData(url) {
    const response = await fetch(url);
    
    if (!response.ok) {
        throw new Error(`Errore HTTP! Status: ${response.status}`);
    }
    
    return await response.json();
}

async function loadMyGames() {
    const container = document.querySelector(".div2");
    const oldGames = document.querySelectorAll(".gamebox");
    oldGames.forEach(box => box.remove());

    const data = await getData("http://localhost:8080/api/users/76767/topPlayedGames"); 

    if (!data) return;

    data.forEach(game=>{
        const gameHTML = `
            <div class="gamebox">
                <div class="gameImageBox">
                    <img src="${game.img}" alt="${game.title}" class="gameimage">
                </div>
                <div class="gameInfo">
                    <h2 class="gameTitle">${game.title}</h2>
                    <h3 class="gameHour">⏱︎ ${game.hours}h played</h3>
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', gameHTML);
    })
}
async function loadSuggestedGamesByGenere() {
    const container = document.querySelector(".sgbox");
    const oldSuggestGames = document.querySelectorAll(".gameSugg");
    oldSuggestGames.forEach(box => box.remove());

    const data = await getData("http://localhost:8080/api/recommendations/content/76767"); 
    data.forEach(game =>{
        const gameHTML= `
            <div class="gameSugg">
                <div class="gameImageBox">
                    <img src="${game.img}" alt="${game.title}" class="gameimage">
                </div>
                <h2 class="gameTitleSugg">${game.title}</h2>
                <h3 class="motivationSugg">Suggested based on your favorite generes</h3>
                <div class="button-container">
                <button class="add-btn" data-title="${game.title}" data-img="${game.img}">+</button>
            </div>
        </div>
        `;
        container.insertAdjacentHTML('beforeend', gameHTML);
    })
}
async function loadSuggestedGamers() {
    const container = document.querySelector(".spbox");
    const oldPerson = document.querySelectorAll(".personSugg");
    oldPerson.forEach(box => box.remove());

    const data = await getData("http://localhost:8080/api/recommendations/people/76767"); 
    data.forEach(person => {
        const personHTML = `
        <div class="personSugg">
                         <div class="avatar">
                            <div class="avatarImageContainer">
                                <img src="./src/avatar.webp" alt="./src/avatar.webp" class="avatarImage">
                            </div>
                        </div>
                        <h1 class="userName">${person.username}</h1>
                        <div class="motivationSugg">Suggested for you</div>
                        <div class="button-container">
                            <a href="#" class="add-btn">♡</a>
                        </div>
                        `;
        container.insertAdjacentHTML('beforeend', personHTML);
    })
}

function setupInteraction() {
    const suggestionsContainer = document.querySelector(".sgbox");
    const myGamesContainer = document.querySelector(".div2");

    suggestionsContainer.addEventListener('click', function(e) {
        if (e.target.classList.contains('add-btn')) {
            
            const gameTitle = e.target.dataset.title;
            const gameImg = e.target.dataset.img;


            const cardToRemove = e.target.closest('.gameSugg');
            cardToRemove.remove();

            const newGameHTML = `
                <div class="gamebox">
                    <div class="gameImageBox">
                        <img src="${gameImg}" alt="${gameTitle}" class="gameimage">
                    </div>
                    <div class="gameInfo">
                        <h2 class="gameTitle">${gameTitle}</h2>
                        <h3 class="gameHour">⏱︎ 0h played</h3>
                    </div>
                </div>
            `;
            myGamesContainer.insertAdjacentHTML('beforeend', newGameHTML);
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    loadMyGames();
    loadSuggestedGamesByGenere();
    loadSuggestedGamers();
    setupInteraction();
});