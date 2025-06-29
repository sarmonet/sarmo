// pages/api/analytics/browsers.ts
import { BetaAnalyticsDataClient } from '@google-analytics/data'
import fs from 'fs'
import type { NextApiRequest, NextApiResponse } from 'next'
import os from 'os'
import path from 'path'

const propertyId = process.env.GA4_PROPERTY_ID
const credentialsJson = process.env.GOOGLE_APPLICATION_CREDENTIALS_JSON

const handler = async (req: NextApiRequest, res: NextApiResponse) => {
	if (!propertyId) {
		console.error('GA4 Property ID не настроен')
		return res.status(500).json({ error: 'GA4 Property ID не настроен' })
	}
	if (!credentialsJson) {
		console.error('GOOGLE_APPLICATION_CREDENTIALS_JSON не настроен')
		return res
			.status(500)
			.json({ error: 'Учетные данные для GA4 API не настроены' })
	}

	let tempKeyPath: string | undefined

	try {
		tempKeyPath = path.join(os.tmpdir(), `ga4-key-${Date.now()}.json`)
		fs.writeFileSync(tempKeyPath, credentialsJson)
		process.env.GOOGLE_APPLICATION_CREDENTIALS = tempKeyPath

		const analyticsDataClient = new BetaAnalyticsDataClient()

		const startDate = (req.query.startDate as string) || '7daysAgo'
		const endDate = (req.query.endDate as string) || 'today'

		const metrics = [{ name: 'totalUsers' }]
		const dimensions = [{ name: 'browser' }]

		const [response] = await analyticsDataClient.runReport({
			property: `properties/${propertyId}`,
			dateRanges: [{ startDate, endDate }],
			metrics,
			dimensions,
			limit: 10,

			orderBys: [{ metric: { metricName: 'totalUsers' }, desc: true }],
		})

		const reportData =
			response.rows?.map(row => ({
				browser: row.dimensionValues?.[0]?.value,
				users: row.metricValues?.[0]?.value,
			})) || []

		res.status(200).json(reportData)
	} catch (error: unknown) {
		console.error('Ошибка в API-маршруте /api/analytics/browsers:', error)
		let detailsMessage: string
		if (error instanceof Error) {
			detailsMessage = error.message
		} else if (
			typeof error === 'object' &&
			error !== null &&
			'details' in error &&
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
			typeof (error as any).details === 'string'
		) {
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
			detailsMessage = (error as any).details
		} else {
			detailsMessage = String(error)
		}
		res.status(500).json({
			error: 'Не удалось получить данные о браузерах',
			details: detailsMessage,
		})
	} finally {
		if (tempKeyPath && fs.existsSync(tempKeyPath)) {
			fs.unlinkSync(tempKeyPath)
			delete process.env.GOOGLE_APPLICATION_CREDENTIALS
		}
	}
}

export default handler
