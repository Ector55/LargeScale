async function getData(url) {
    const response = await fetch(url);
    
    if (!response.ok) {
        throw new Error(`Errore HTTP! Status: ${response.status}`);
    }
    
    return await response.json();
}

async function loadMyGames() {
    const container = document.querySelector(".games-container");
    const oldGames = document.querySelectorAll(".gamebox");
    oldGames.forEach(box => box.remove());

    const data = await getData("http://localhost:8080/api/users/76767/library"); 

    if (!data) return;

    data.forEach(game=>{
        const gameHTML = `
            <div class="gameBox">
                        <img src="${game.img}" alt="Elden Ring" title="${game.title}" class="game-image">
                    </div>
        `;
        container.insertAdjacentHTML('beforeend', gameHTML);
    })
}
document.addEventListener('DOMContentLoaded', () => {
    loadMyGames();
});