
import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
  reactStrictMode: true,

  transpilePackages: ['swiper', 'swiper-react'],

  async headers() {
    return [
      {
        source: "/login",
        headers: [
          {
            key: "Access-Control-Allow-Origin",
            value: "*",
          },
        ],
      },
    ];
  },

  async rewrites() {
    return [
      {
        source: '/api/auth/:path*',
        destination: '/api/auth/:path*',
      },
      {
        source: '/auth/:path*',
        destination: 'http://77.221.152.120:8081/auth/:path*',
      },
    ];
  },

  i18n: {
    defaultLocale: 'ru',
    locales: ['ru', 'en', 'uz'],
    localeDetection: false,
  },

  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: '**', 
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'versii.if.ua',
        pathname: '/wp-content/uploads/**',
      },
      {
        protocol: 'https',
        hostname: 'encrypted-tbn0.gstatic.com',
        pathname: '/images/**',
      },
      {
        protocol: 'https',
        hostname: 's.mind.ua',
        pathname: '/img/**',
      },
      {
        protocol: 'https',
        hostname: 'i5.walmartimages.ca',
        pathname: '/images/**',
      },
      {
        protocol: 'https',
        hostname: 'uk.gymshark.com',
        pathname: '/_next/image/**',
      },
      {
        protocol: 'https',
        hostname: 'cdn-magazine.nutrabay.com',
        pathname: '/wp-content/uploads/**',
      },
      {
        protocol: 'https',
        hostname: 'upload.wikimedia.org',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'images.cnscdn.com',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'rentacar.zp.ua',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'encrypted-tbn0.gstatic.com',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'example.com', 
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'sarmo.net',
        pathname: '/**',
      },
    ],
  },
};

export default nextConfig;