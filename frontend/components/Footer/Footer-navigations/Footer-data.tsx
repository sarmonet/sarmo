import { ISections } from './Footer.interface'
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const GetFooterSections  = (t: any): ISections[] => [
  {
    title: t('footerSections.main'),
    link: "/",
  },
  {
    title: t('footerSections.listings'),
    link: "/catalog",
  },
  {
    title: t('footerSections.subscriptions'),
    link: "/",
  },
  {
    title: t('footerSections.blog'),
    adminOnly: true,
    link: "/blog",
  },
  {
    title: t('footerSections.chat'),
    adminOnly: true,
    link: "/chat",
  },
];


    // eslint-disable-next-line @typescript-eslint/no-explicit-any
export const GetFooterContact = (t: any): ISections[] => [
  {
    title: "+998 (90) 177 11 13", 
    link: "/",
  },
  {
    title: "+998 (90) 176 11 13", 
    link: "/",
  },
  {
    title: t('footer.contacts'), 
    link: "/",
  },
];