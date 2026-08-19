(function () {
    const storageKey = "calendar-theme";
    const root = document.documentElement;
    const saved = localStorage.getItem(storageKey);
    const initial = saved === "dark" || saved === "light" ? saved : (window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light");

    function applyTheme(theme) {
        root.dataset.theme = theme;
        root.style.colorScheme = theme;
        document.querySelectorAll(".theme-toggle").forEach((button) => {
            const dark = theme === "dark";
            button.setAttribute("aria-pressed", String(dark));
            button.setAttribute("aria-label", dark ? "ライトモードに切り替える" : "ダークモードに切り替える");
            button.querySelector("span").textContent = dark ? "☀" : "☾";
        });
    }

    applyTheme(initial);
    document.addEventListener("DOMContentLoaded", () => {
        applyTheme(initial);
        document.querySelectorAll(".theme-toggle").forEach((button) => {
            button.addEventListener("click", () => {
                const next = root.dataset.theme === "dark" ? "light" : "dark";
                localStorage.setItem(storageKey, next);
                applyTheme(next);
            });
        });
    });
})();
