/**
 * YÁYA Web Components - Atomic Design System
 * Architecture: Atoms -> Molecules -> Organisms
 */

const YayaComponents = {
    // ⚛️ ÁTOMOS: Los bloques más básicos (DRY Styles)
    atoms: {
        colors: {
            red: "#E85C5C",
            navy: "#1E2A38",
            salmon: "#F26B6B"
        },
        buttonPrimary: (text, link = "#") => `
            <a href="${link}" class="inline-block bg-yayaRed text-white px-6 md:px-8 py-3 md:py-4 rounded-2xl font-bold text-base md:text-lg hover:scale-105 transition shadow-xl shadow-yayaRed/30 text-center w-full sm:w-auto">
                ${text}
            </a>
        `,
        buttonSecondary: (text, link = "#") => `
            <a href="${link}" class="inline-block bg-white dark:bg-darkSurface border-2 border-yayaNavy dark:border-white/10 text-yayaNavy dark:text-white px-6 md:px-8 py-3 md:py-4 rounded-2xl font-bold text-base md:text-lg hover:bg-yayaNavy hover:text-white dark:hover:bg-white dark:hover:text-black transition text-center w-full sm:w-auto">
                ${text}
            </a>
        `,
        badge: (text, colorClass = "bg-yayaRed/10 text-yayaRed") => `
            <span class="px-3 py-1 rounded-full text-[10px] md:text-xs font-black uppercase tracking-widest ${colorClass}">
                ${text}
            </span>
        `
    },

    // 🧬 MOLÉCULAS: Grupos de átomos funcionales
    molecules: {
        featureCard: (icon, title, desc) => `
            <div class="p-6 md:p-8 rounded-3xl bg-white dark:bg-white/5 transition border border-transparent hover:border-gray-100 dark:hover:border-white/10 group shadow-sm">
                <div class="w-14 h-14 md:w-16 md:h-16 bg-red-50 dark:bg-yayaRed/10 text-yayaRed rounded-2xl flex items-center justify-center mx-auto mb-6 group-hover:bg-yayaRed group-hover:text-white transition-colors duration-500">
                    ${icon}
                </div>
                <h3 class="text-lg md:text-xl font-bold text-yayaNavy dark:text-white mb-4">${title}</h3>
                <p class="text-gray-500 dark:text-gray-400 leading-relaxed text-sm">${desc}</p>
            </div>
        `,
        stepItem: (num, title, desc) => `
            <div class="text-center group">
                <div class="text-3xl md:text-4xl font-black text-yayaRed mb-3 md:mb-4 group-hover:scale-110 transition duration-300">${num}</div>
                <h4 class="font-bold mb-2 dark:text-white text-yayaNavy text-sm md:text-base">${title}</h4>
                <p class="text-[11px] md:text-sm text-gray-500 dark:text-gray-400">${desc}</p>
            </div>
        `
    },

    // 🏗️ ORGANISMOS: Secciones complejas orquestadas
    organisms: {
        navbar: (activePage = "") => {
            const prefix = activePage === "home" ? "" : "index.html";
            return `
                <nav class="fixed w-full z-50 bg-white/80 dark:bg-darkBg/80 backdrop-blur-md border-b border-gray-100 dark:border-white/5">
                    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                        <div class="flex justify-between h-16 md:h-20 items-center">
                            <div class="flex items-center space-x-2">
                                <a href="index.html" class="flex items-center space-x-3 group">
                                    <img src="assets/images/logo_yaya_typographic.png" alt="YÁYA" class="h-8 md:h-10 w-auto group-hover:scale-105 transition-transform duration-300">
                                </a>
                            </div>
                            <!-- Menu desktop -->
                            <div class="hidden lg:flex space-x-8">
                                <a href="${prefix}#features" class="text-sm font-bold text-yayaNavy dark:text-gray-300 hover:text-yayaRed transition">Beneficios</a>
                                <a href="manuales.html" class="text-sm font-bold text-yayaNavy dark:text-gray-300 hover:text-yayaRed transition">Manual de Uso</a>
                                <a href="tecnica.html" class="text-sm font-bold text-yayaNavy dark:text-gray-300 hover:text-yayaRed transition">Arquitectura</a>
                            </div>
                            <div class="flex items-center space-x-2 md:space-x-4">
                                <button onclick="toggleTheme()" class="p-2 md:p-2.5 rounded-xl bg-gray-100 dark:bg-darkSurface hover:bg-gray-200 dark:hover:bg-white/10 transition-colors">
                                    <svg class="w-5 h-5 block dark:hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"></path></svg>
                                    <svg class="w-5 h-5 hidden dark:block text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 18v1m9-11h1M3 12h1m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                                </button>
                                <a href="https://play.google.com/store/apps/details?id=com.bhplusplus.yaya" class="hidden sm:block bg-yayaNavy dark:bg-yayaRed text-white px-4 md:px-6 py-2 md:2.5 rounded-full text-xs md:text-sm font-extrabold hover:opacity-90 transition shadow-lg">Descargar App</a>

                                <!-- Mobile Menu Toggle -->
                                <button onclick="toggleMobileMenu()" class="lg:hidden p-2 text-yayaNavy dark:text-white">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6h7"></path></svg>
                                </button>
                            </div>
                        </div>
                    </div>
                    <!-- Mobile Menu Container -->
                    <div id="mobile-menu" class="hidden lg:hidden bg-white dark:bg-darkBg border-t dark:border-white/5 px-4 py-6 space-y-4 shadow-xl animate-slide-down">
                         <a href="${prefix}#features" onclick="toggleMobileMenu()" class="block text-base font-bold text-yayaNavy dark:text-gray-300">Beneficios</a>
                         <a href="manuales.html" onclick="toggleMobileMenu()" class="block text-base font-bold text-yayaNavy dark:text-gray-300">Manual de Uso</a>
                         <a href="tecnica.html" onclick="toggleMobileMenu()" class="block text-base font-bold text-yayaNavy dark:text-gray-300">Arquitectura</a>
                    </div>
                </nav>
            `;
        },
        footer: () => `
            <footer class="py-12 md:py-16 bg-white dark:bg-darkBg border-t border-gray-100 dark:border-white/5 transition-colors">
                <div class="max-w-7xl mx-auto px-4">
                    <div class="flex flex-col md:flex-row justify-between items-center mb-8 md:mb-12">
                        <div class="flex items-center space-x-3 mb-6 md:mb-0">
                            <img src="assets/images/ic_logo.png" alt="YÁYA Isotipo" class="h-6 md:h-8 w-auto">
                            <span class="text-xl md:text-3xl font-black text-yayaNavy dark:text-white italic">YÁYA</span>
                        </div>
                        <div class="flex flex-wrap justify-center gap-4 md:gap-8 text-xs md:text-sm font-bold text-gray-500">
                            <a href="terminos.html" class="hover:text-yayaRed transition">Términos</a>
                            <a href="privacidad.html" class="hover:text-yayaRed transition">Privacidad</a>
                            <a href="manuales.html" class="hover:text-yayaRed transition">Manual de Uso</a>
                            <a href="tecnica.html" class="hover:text-yayaRed transition">Técnico</a>
                        </div>
                    </div>
                    <div class="pt-6 md:pt-8 border-t border-gray-100 dark:border-white/5 text-center">
                        <p class="text-gray-400 text-[10px] md:text-xs tracking-widest uppercase">© 2026 BH++ Team • Senior Engineering SENA</p>
                    </div>
                </div>
            </footer>
        `
    }
};

// Global Theme Logic (Stateless Utility)
function toggleTheme() {
    if (document.documentElement.classList.contains('dark')) {
        document.documentElement.classList.remove('dark');
        localStorage.theme = 'light';
    } else {
        document.documentElement.classList.add('dark');
        localStorage.theme = 'dark';
    }
}

// Mobile Menu Toggle
function toggleMobileMenu() {
    const menu = document.getElementById('mobile-menu');
    if (menu) {
        menu.classList.toggle('hidden');
    }
}

// Bootstrap (Initialization)
document.addEventListener('DOMContentLoaded', () => {
    // Apply persistence theme
    if (localStorage.theme === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
        document.documentElement.classList.add('dark')
    }

    // Inyectar Navbar y Footer si existen placeholders
    const navEl = document.getElementById('yaya-navbar');
    if (navEl) navEl.innerHTML = YayaComponents.organisms.navbar(navEl.dataset.page);

    const footEl = document.getElementById('yaya-footer');
    if (footEl) footEl.innerHTML = YayaComponents.organisms.footer();
});
