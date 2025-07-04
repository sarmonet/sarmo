// pages/_document.js
import Document, { Head, Html, Main, NextScript } from 'next/document'
import Script from 'next/script'
import React from 'react'

class MyDocument extends Document {
	render() {
		const GTM_ID = 'GTM-N3GKJ7WH'

		return (
			<Html lang='ru'>
				<Head>
					<Script
						id='gtm-script'
						strategy='afterInteractive'
						dangerouslySetInnerHTML={{
							__html: `(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
              new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
              j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
              'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
              })(window,document,'script','dataLayer','${GTM_ID}');`,
						}}
					/>
				</Head>
				<body>
					<noscript>
						<iframe
							src={`https://www.googletagmanager.com/ns.html?id=${GTM_ID}`}
							height='0'
							width='0'
							style={{ display: 'none', visibility: 'hidden' }}
						></iframe>
					</noscript>

					<Main />
					<NextScript />
				</body>
			</Html>
		)
	}
}

export default MyDocument
