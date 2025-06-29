

import { IMenuLink } from './menu-item/menu-item.interface'

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
export const getMenu = (t: any): IMenuLink[] => [
  {
    link: '/catalog',
    name: t('menu.catalog')
  },
  {
    link: '/blog',
    name: t('menu.blog'),
    adminOnly: true
  },
  {
    link: '/news',
    name: t('menu.news')
  },
  {
    link: '/',
    name: t('menu.analytics')
  }
];

