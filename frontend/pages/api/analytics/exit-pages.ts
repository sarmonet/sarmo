import { BetaAnalyticsDataClient } from '@google-analytics/data'
import fs from 'fs'
import type { NextApiRequest, NextApiResponse } from 'next'
import os from 'os'
import path from 'path'

const propertyId = process.env.GA4_PROPERTY_ID
const credentialsJson = process.env.GOOGLE_APPLICATION_CREDENTIALS_JSON

const handler = async (req: NextApiRequest, res: NextApiResponse) => {
	if (!propertyId || !credentialsJson) {
		return res.status(500).json({ error: 'GA4 конфигурация отсутствует' })
	}

	let tempKeyPath: string | undefined

	try {
		tempKeyPath = path.join(os.tmpdir(), `ga4-key-${Date.now()}.json`)
		fs.writeFileSync(tempKeyPath, credentialsJson)
		process.env.GOOGLE_APPLICATION_CREDENTIALS = tempKeyPath

		const client = new BetaAnalyticsDataClient()

		const startDate = (req.query.startDate as string) || '7daysAgo'
		const endDate = (req.query.endDate as string) || 'today'
		const limit = parseInt((req.query.limit as string) || '10', 10)

		const [response] = await client.runReport({
			property: `properties/${propertyId}`,
			dateRanges: [{ startDate, endDate }],
			dimensions: [{ name: 'pagePath' }],

			metrics: [{ name: 'exits' }, { name: 'views' }],
			orderBys: [{ metric: { metricName: 'exits' }, desc: true }],
		})

		console.log(
			'Запрос к GA4:',
			JSON.stringify(
				{
					property: `properties/${propertyId}`,
					dateRanges: [{ startDate, endDate }],
					dimensions: [{ name: 'pagePath' }],
					metrics: [{ name: 'exits' }, { name: 'views' }],
					orderBys: [{ metric: { metricName: 'exits' }, desc: true }],
					limit: limit,
				},
				null,
				2
			)
		)

		const data =
			response.rows?.map(row => {
				const exits = parseInt(row.metricValues?.[0]?.value || '0', 10)
				const views = parseInt(row.metricValues?.[1]?.value || '0', 10)

				const exitRate = views > 0 ? (exits / views) * 100 : 0

				return {
					pagePath: row.dimensionValues?.[0]?.value || 'N/A',
					exits: exits.toString(),
					views: views.toString(),
					exitRate: exitRate.toFixed(2),
				}
			}) || []

		res.status(200).json(data)
	} catch (error: unknown) {
		console.error('Ошибка в exit-pages API:', error)
		res.status(500).json({
			error: 'Не удалось получить страницы выхода',
			details: String(error),
		})
	} finally {
		if (tempKeyPath && fs.existsSync(tempKeyPath)) {
			fs.unlinkSync(tempKeyPath)
			delete process.env.GOOGLE_APPLICATION_CREDENTIALS
		}
	}
}

export default handler
