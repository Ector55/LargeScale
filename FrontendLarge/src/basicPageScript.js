document.addEventListener('DOMContentLoaded', () => {
    const slides = document.querySelectorAll('.slide');
    let currentIndex = 0;
    const intervalTime = 3000; 

    function nextSlide() {
        const currentSlide = slides[currentIndex];
        
        const nextIndex = (currentIndex + 1) % slides.length;
        const nextSlide = slides[nextIndex];

        currentSlide.classList.remove('active');
        currentSlide.classList.add('exit');

        nextSlide.classList.add('active');
        nextSlide.classList.remove('exit');

        setTimeout(() => {
            currentSlide.classList.remove('exit');
        }, 600);

        currentIndex = nextIndex;
    }

    setInterval(nextSlide, intervalTime);
});