import { useBounceRateData } from '@/components/hooks/useBounceRateData'
import { useBrowsersData } from '@/components/hooks/useBrowsersData'
import { useDevicesData } from '@/components/hooks/useDevicesData'
import { useExitPagesData } from '@/components/hooks/useExitPagesData'
import { useTrafficSourcesData } from '@/components/hooks/useTrafficSourcesData'
import { useUsersOverview } from '@/components/hooks/useUsersOverview'

import { useAdViewsData } from '@/components/hooks/useAdViewsData'
import { useGeoData } from '@/components/hooks/useGeoData'

import { Card, CardContent } from '@/components/ui/card'
import {
	Bar,
	BarChart,
	CartesianGrid,
	Cell,
	Legend,
	Line,
	LineChart,
	Pie,
	PieChart,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts'

import { useTranslation } from 'react-i18next'

const PIE_COLORS = [
	'#0088FE',
	'#00C49F',
	'#FFBB28',
	'#FF8042',
	'#AF19FF',
	'#83A6ED',
	'#8DD1E1',
]
const BAR_COLOR = '#8884d8'

export const AnalyticsDashboard = () => {
	const { t } = useTranslation()

	const {
		data: usersOverviewData,
		loading: usersLoading,
		error: usersError,
	} = useUsersOverview()
	const {
		data: browsersData,
		loading: browsersLoading,
		error: browsersError,
	} = useBrowsersData()
	const {
		data: devicesData,
		loading: devicesLoading,
		error: devicesError,
	} = useDevicesData()
	const {
		data: trafficSourcesData,
		loading: trafficSourcesLoading,
		error: trafficSourcesError,
	} = useTrafficSourcesData()
	const {
		data: bounceRateChartData,
		loading: bounceRateLoading,
		error: bounceRateError,
	} = useBounceRateData()
	const {
		data: exitPagesData,
		loading: exitPagesLoading,
		error: exitPagesError,
	} = useExitPagesData()

	const { data: geoData, loading: geoLoading, error: geoError } = useGeoData()
	const {
		data: adViewsData,
		loading: adViewsLoading,
		error: adViewsError,
	} = useAdViewsData()

	const chartUsersOverview = usersOverviewData.map(d => ({
		day: d.date,
		active: Number(d.totalUsers),
		new: Number(d.newUsers),
	}))

	const chartBrowsersData = browsersData.map(d => ({
		name: d.browser,
		value: Number(d.users),
	}))
	const totalBrowserUsers = chartBrowsersData.reduce(
		(sum, item) => sum + item.value,
		0
	)

	const chartDevicesData = devicesData.reduce((acc, current) => {
		let deviceName = current.operatingSystem
		if (
			current.deviceCategory === 'mobile' &&
			current.operatingSystem === 'Android'
		) {
			deviceName = t('analyticsDashboard.androidPhone')
		} else if (
			current.deviceCategory === 'mobile' &&
			current.operatingSystem === 'iOS'
		) {
			deviceName = t('analyticsDashboard.iOS')
		} else if (
			current.operatingSystem === 'macOS' ||
			current.operatingSystem === 'Macintosh'
		) {
			deviceName = t('analyticsDashboard.macbook')
		} else if (
			current.deviceCategory === 'tablet' &&
			current.operatingSystem === 'iOS'
		) {
			deviceName = t('analyticsDashboard.iPad')
		} else if (
			current.deviceCategory === 'tablet' &&
			current.operatingSystem === 'Android'
		) {
			deviceName = t('analyticsDashboard.androidTablet')
		}

		const existingEntry = acc.find(item => item.name === deviceName)
		if (existingEntry) {
			existingEntry.value += Number(current.users)
		} else {
			acc.push({ name: deviceName, value: Number(current.users) })
		}
		return acc
	}, [] as { name: string; value: number }[])
	const totalDeviceUsers = chartDevicesData.reduce(
		(sum, item) => sum + item.value,
		0
	)

	const mapChannelGroup = (channel: string) => {
		switch (channel) {
			case 'Organic Search':
				return t('analyticsDashboard.google')
			case 'Social':
				return t('analyticsDashboard.socialNetworks')
			case 'Paid Search':
			case 'Paid Shopping':
			case 'Paid Video':
			case 'Display':
				return t('analyticsDashboard.ads')
			default:
				return t('analyticsDashboard.other')
		}
	}

	const chartTrafficSourcesData = trafficSourcesData.reduce((acc, current) => {
		const mappedChannel = mapChannelGroup(current.channelGroup || 'N/A')
		const existingEntry = acc.find(item => item.name === mappedChannel)
		if (existingEntry) {
			existingEntry.value += Number(current.users)
		} else {
			acc.push({ name: mappedChannel, value: Number(current.users) })
		}
		return acc
	}, [] as { name: string; value: number }[])

	const chartExitPagesData = exitPagesData.map(d => ({
		name: d.pagePath,
		value: Number(d.exits),
	}))

	const chartGeoData = geoData.map(d => ({
		name: d.country,
		value: Number(d.users),
	}))
	const totalGeoUsers = chartGeoData.reduce((sum, item) => sum + item.value, 0)

	const chartAdViewsData = adViewsData.map(d => ({
		name: d.pagePath,
		value: Number(d.views),
	}))

	return (
		<div className='grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 p-4'>
			<Card className='col-span-2'>
				<CardContent>
					<h3 className='text-xl font-semibold mb-4'>
						{t('analyticsDashboard.userDynamics')}
					</h3>
					{usersLoading && <p>{t('analyticsDashboard.loadingData')}</p>}
					{usersError && (
						<p className='text-red-500'>
							{t('analyticsDashboard.error')} {usersError}
						</p>
					)}
					{!usersLoading && !usersError && chartUsersOverview.length > 0 ? (
						<ResponsiveContainer width='100%' height={300}>
							<LineChart data={chartUsersOverview}>
								<CartesianGrid strokeDasharray='3 3' />
								<XAxis dataKey='day' />
								<YAxis />
								<Tooltip />
								<Legend />
								<Line
									type='monotone'
									dataKey='active'
									stroke='#8884d8'
									name={t('analyticsDashboard.active')}
								/>
								<Line
									type='monotone'
									dataKey='new'
									stroke='#82ca9d'
									name={t('analyticsDashboard.new')}
								/>
							</LineChart>
						</ResponsiveContainer>
					) : (
						!usersLoading &&
						!usersError && <p>{t('analyticsDashboard.noDataDisplay')}</p>
					)}
				</CardContent>
			</Card>

			<Card>
				<CardContent>
					<h3 className='text-xl font-semibold mb-4'>
						{t('analyticsDashboard.browsers')}
					</h3>
					{browsersLoading && <p>{t('analyticsDashboard.loadingData')}</p>}
					{browsersError && (
						<p className='text-red-500'>
							{t('analyticsDashboard.error')} {browsersError}
						</p>
					)}
					{!browsersLoading &&
					!browsersError &&
					chartBrowsersData.length > 0 ? (
						<ResponsiveContainer width='100%' height={300}>
							<PieChart>
								<Pie
									data={chartBrowsersData}
									dataKey='value'
									nameKey='name'
									cx='50%'
									cy='50%'
									outerRadius={100}
									label={({ name, value }) =>
										`${name} ${(
											(Number(value) / totalBrowserUsers) *
											100
										).toFixed(0)}%`
									}
									labelLine={false}
								>
									{chartBrowsersData.map((entry, index) => (
										<Cell
											key={`cell-${index}`}
											fill={PIE_COLORS[index % PIE_COLORS.length]}
										/>
									))}
								</Pie>
								<Tooltip />
								<Legend />
							</PieChart>
						</ResponsiveContainer>
					) : (
						!browsersLoading &&
						!browsersError && <p>{t('analyticsDashboard.noBrowserData')}</p>
					)}
				</CardContent>
			</Card>

			<Card>
				<CardContent>
					<h3 className='text-xl font-semibold mb-4'>
						{t('analyticsDashboard.devices')}
					</h3>
					{devicesLoading && <p>{t('analyticsDashboard.loadingData')}</p>}
					{devicesError && (
						<p className='text-red-500'>
							{t('analyticsDashboard.error')} {devicesError}
						</p>
					)}
					{!devicesLoading && !devicesError && chartDevicesData.length > 0 ? (
						<ResponsiveContainer width='100%' height={300}>
							<PieChart>
								<Pie
									data={chartDevicesData}
									dataKey='value'
									nameKey='name'
									cx='50%'
									cy='50%'
									outerRadius={100}
									label={({ name, value }) =>
										`${name} ${(
											(Number(value) / totalDeviceUsers) *
											100
										).toFixed(0)}%`
									}
									labelLine={false}
								>
									{chartDevicesData.map((entry, index) => (
										<Cell
											key={`cell-${index}`}
											fill={PIE_COLORS[index % PIE_COLORS.length]}
										/>
									))}
								</Pie>
								<Tooltip />
								<Legend />
							</PieChart>
						</ResponsiveContainer>
					) : (
						!devicesLoading &&
						!devicesError && <p>{t('analyticsDashboard.noDeviceData')}</p>
					)}
				</CardContent>
			</Card>

			<Card>
				<CardContent>
					<h3 className='text-xl font-semibold mb-4'>
						{t('analyticsDashboard.trafficSources')}
					</h3>
					{trafficSourcesLoading && (
						<p>{t('analyticsDashboard.loadingData')}</p>
					)}
					{trafficSourcesError && (
						<p className='text-red-500'>
							{t('analyticsDashboard.error')} {trafficSourcesError}
						</p>
					)}
					{!trafficSourcesLoading &&
					!trafficSourcesError &&
					chartTrafficSourcesData.length > 0 ? (
						<ResponsiveContainer width='100%' height={300}>
							<BarChart data={chartTrafficSourcesData}>
								<CartesianGrid strokeDasharray='3 3' />
								<XAxis dataKey='name' />
								<YAxis />
								<Tooltip />
								<Legend />
								<Bar
									dataKey='value'
									fill={BAR_COLOR}
									name={t('analyticsDashboard.users')}
								/>
							</BarChart>
						</ResponsiveContainer>
					) : (
						!trafficSourcesLoading &&
						!trafficSourcesError && (
							<p>{t('analyticsDashboard.noTrafficSourceData')}</p>
						)
					)}
				</CardContent>
			</Card>

			<Card className='col-span-2'>
				<CardContent>
					<h3 className='text-xl font-semibold mb-4'>
						{t('analyticsDashboard.bounceRate')}
					</h3>
					{bounceRateLoading && <p>{t('analyticsDashboard.loadingData')}</p>}
					{bounceRateError && (
						<p className='text-red-500'>
							{t('analyticsDashboard.error')} {bounceRateError}
						</p>
					)}
					{!bounceRateLoading &&
					!bounceRateError &&
					bounceRateChartData.length > 0 ? (
						<ResponsiveContainer width='100%' height={300}>
							<LineChart data={bounceRateChartData}>
								<CartesianGrid strokeDasharray='3 3' />
								<XAxis dataKey='date' />
								<YAxis domain={[0, 100]} />
								<Tooltip
									formatter={(value: number) => `${value.toFixed(2)}%`}
								/>
								<Legend />
								<Line
									type='monotone'
									dataKey='bounceRate'
									stroke='#FF5733'
									name={t('analyticsDashboard.bounceRate')}
								/>
							</LineChart>
						</ResponsiveContainer>
					) : (
						!bounceRateLoading &&
						!bounceRateError && (
							<p>{t('analyticsDashboard.noBounceRateData')}</p>
						)
					)}
				</CardContent>
			</Card>

			<Card>
				<CardContent>
					<h3 className='text-xl font-semibold mb-4'>
						{t('analyticsDashboard.exitPages')}
					</h3>
					{exitPagesLoading && <p>{t('analyticsDashboard.loadingData')}</p>}
					{exitPagesError && (
						<p className='text-red-500'>
							{t('analyticsDashboard.error')} {exitPagesError}
						</p>
					)}
					{!exitPagesLoading &&
					!exitPagesError &&
					chartExitPagesData.length > 0 ? (
						<ResponsiveContainer width='100%' height={300}>
							<BarChart data={chartExitPagesData} layout='vertical'>
								<CartesianGrid strokeDasharray='3 3' />
								<XAxis type='number' />
								<YAxis type='category' dataKey='name' width={150} /> <Tooltip />
								<Legend />
								<Bar
									dataKey='value'
									fill='#3375FF'
									name={t('analyticsDashboard.exits')}
								/>
							</BarChart>
						</ResponsiveContainer>
					) : (
						!exitPagesLoading &&
						!exitPagesError && <p>{t('analyticsDashboard.noExitPagesData')}</p>
					)}
				</CardContent>
			</Card>

			<Card>
				<CardContent>
					<h3 className='text-xl font-semibold mb-4'>
						{t('analyticsDashboard.userGeography')}
					</h3>
					{geoLoading && <p>{t('analyticsDashboard.loadingData')}</p>}
					{geoError && (
						<p className='text-red-500'>
							{t('analyticsDashboard.error')} {geoError}
						</p>
					)}
					{!geoLoading && !geoError && chartGeoData.length > 0 ? (
						<ResponsiveContainer width='100%' height={300}>
							<PieChart>
								<Pie
									data={chartGeoData}
									dataKey='value'
									nameKey='name'
									cx='50%'
									cy='50%'
									outerRadius={100}
									label={({ name, value }) =>
										`${name} ${((Number(value) / totalGeoUsers) * 100).toFixed(
											0
										)}%`
									}
									labelLine={false}
								>
									{chartGeoData.map((entry, index) => (
										<Cell
											key={`cell-${index}`}
											fill={PIE_COLORS[index % PIE_COLORS.length]}
										/>
									))}
								</Pie>
								<Tooltip />
								<Legend />
							</PieChart>
						</ResponsiveContainer>
					) : (
						!geoLoading &&
						!geoError && <p>{t('analyticsDashboard.noGeoData')}</p>
					)}
				</CardContent>
			</Card>

			<Card>
				<CardContent>
					<h3 className='text-xl font-semibold mb-4'>
						{t('analyticsDashboard.listingViews')}
					</h3>
					{adViewsLoading && <p>{t('analyticsDashboard.loadingData')}</p>}
					{adViewsError && (
						<p className='text-red-500'>
							{t('analyticsDashboard.error')} {adViewsError}
						</p>
					)}
					{!adViewsLoading && !adViewsError && chartAdViewsData.length > 0 ? (
						<ResponsiveContainer width='100%' height={300}>
							<BarChart data={chartAdViewsData} layout='vertical'>
								<CartesianGrid strokeDasharray='3 3' />
								<XAxis type='number' />
								<YAxis
									type='category'
									dataKey='name'
									width={200}
									tickFormatter={value => {
										const maxLength = 30
										return value.length > maxLength
											? value.substring(0, maxLength - 3) + '...'
											: value
									}}
								/>
								<Tooltip />
								<Legend />
								<Bar
									dataKey='value'
									fill='#4CAF50'
									name={t('analyticsDashboard.views')}
								/>
							</BarChart>
						</ResponsiveContainer>
					) : (
						!adViewsLoading &&
						!adViewsError && <p>{t('analyticsDashboard.noAdViewsData')}</p>
					)}
				</CardContent>
			</Card>
		</div>
	)
}
