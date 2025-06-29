// pages/api/analytics/ad-views.ts

import { BetaAnalyticsDataClient } from '@google-analytics/data'
import fs from 'fs'
import type { NextApiRequest, NextApiResponse } from 'next'
import os from 'os'
import path from 'path'

const propertyId = process.env.GA4_PROPERTY_ID
const credentialsJson = process.env.GOOGLE_APPLICATION_CREDENTIALS_JSON

const handler = async (req: NextApiRequest, res: NextApiResponse) => {
	console.log('[API/ad-views] Request received.')

	if (!propertyId || !credentialsJson) {
		console.error('[API/ad-views] ERROR: GA4 configuration missing.')
		return res.status(500).json({
			error: 'GA4 конфигурация отсутствует. Проверьте переменные окружения.',
		})
	}

	let tempKeyPath: string | undefined

	try {
		tempKeyPath = path.join(os.tmpdir(), `ga4-key-${Date.now()}.json`)
		fs.writeFileSync(tempKeyPath, credentialsJson)
		process.env.GOOGLE_APPLICATION_CREDENTIALS = tempKeyPath
		console.log('[API/ad-views] Temporary key file created successfully.')

		const client = new BetaAnalyticsDataClient()
		console.log('[API/ad-views] BetaAnalyticsDataClient initialized.')

		const startDate = (req.query.startDate as string) || '7daysAgo'
		const endDate = (req.query.endDate as string) || 'today'
		const limit = parseInt((req.query.limit as string) || '10', 10)

		const dimensions = [{ name: 'pagePath' }]
		const metrics = [{ name: 'screenPageViews' }]

		// const dimensionFilter = {
		// 	filter: {
		// 		fieldName: 'pagePath',
		// 		stringFilter: {
		// 			matchType: 'CONTAINS',
		// 			value: '/listings/',
		// 		},
		// 	},
		// }

		console.log('[API/ad-views] Preparing GA4 Data API runReport request with:')
		console.log(`  Property ID: ${propertyId}`)
		console.log(`  Date Range: ${startDate} to ${endDate}`)
		console.log(`  Dimensions: pagePath`)
		console.log(`  Metrics: screenPageViews`)
		console.log(`  Limit: ${limit}`)

		const [response] = await client.runReport({
			property: `properties/${propertyId}`,
			dateRanges: [{ startDate, endDate }],
			dimensions: dimensions,
			metrics: metrics,
			orderBys: [{ metric: { metricName: 'screenPageViews' }, desc: true }],
			limit: limit,
		})

		console.log('[API/ad-views] GA4 Data API response received.')

		const data =
			response.rows?.map(row => ({
				pagePath: row.dimensionValues?.[0]?.value || 'N/A',
				views: row.metricValues?.[0]?.value || '0',
			})) || []

		console.log(`[API/ad-views] Processed ${data.length} rows.`)
		res.status(200).json(data)
	} catch (error: unknown) {
		console.error('[API/ad-views] ERROR caught during GA4 API call:')
		console.error(
			'  Full error object:',
			JSON.stringify(error, Object.getOwnPropertyNames(error), 2)
		)
		res.status(500).json({
			error: 'Не удалось получить данные о просмотрах объявлений',
			details: String(error),
		})
	} finally {
		if (tempKeyPath && fs.existsSync(tempKeyPath)) {
			console.log(`[API/ad-views] Deleting temporary key file: ${tempKeyPath}`)
			fs.unlinkSync(tempKeyPath)
			delete process.env.GOOGLE_APPLICATION_CREDENTIALS
			console.log('[API/ad-views] Temporary key file deleted.')
		}
	}
}

export default handler
