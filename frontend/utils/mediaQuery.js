const breakpoints = [320, 480, 768, 1280, 1440];

const [smallMobile, mobile, tablet, desktop, largeDesktop] = breakpoints.map(
  (bp) => `@media (min-width: ${bp}px)`
);

const mq = {
  smallMobile,
  mobile,
  tablet,
  desktop,
  largeDesktop,
};

export default mq;
