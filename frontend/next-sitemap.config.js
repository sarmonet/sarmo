module.exports = {
  siteUrl: "https://reach-ua.mbit-consultants.com", // Базовий URL вашого сайту
  generateRobotsTxt: true, // Автоматичне створення robots.txt
  changefreq: "weekly", // Частота оновлення
  priority: 0.8, // Стандартний пріоритет
  sitemapSize: 5000, // Максимальна кількість URL у файлі sitemap
  alternateRefs: [
    {
      href: "https://reach-ua.mbit-consultants.com", // Англійська версія
      hreflang: "en",
    },
    {
      href: "https://reach-ua.mbit-consultants.com/uk", // Українська версія
      hreflang: "uk",
    },
  ],
  transform: async (config, path) => {
    // Налаштування для кожної сторінки
    const paths = {
      "/": { priority: 1.0 },
      "/services": { priority: 0.8 },
      "/ua-reach": { priority: 0.8 },
    };

    return {
      loc: path, // Посилання на сторінку
      changefreq: config.changefreq, // Частота змін
      priority: paths[path]?.priority || config.priority, // Пріоритет сторінки
      lastmod: new Date().toISOString(), // Останнє оновлення
      alternateRefs: config.alternateRefs.map((altRef) => ({
        ...altRef,
        href: altRef.href, // Тут додається зайвий сегмент
      })),
    };
  },
  robotsTxtOptions: {
    policies: [
      { userAgent: "*", allow: "/" }, // Дозволяємо індексацію всіх сторінок
    ],
  },
};
