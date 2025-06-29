// components/hooks/useDevicesData.ts
import { useCallback, useEffect, useState } from 'react'

interface DeviceDataRow {
  deviceCategory: string;
  operatingSystem: string;
  users: string;
}

interface UseDevicesDataResult {
  data: DeviceDataRow[];
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

export const useDevicesData = (startDate = '7daysAgo', endDate = 'today'): UseDevicesDataResult => {
  const [data, setData] = useState<DeviceDataRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(`/api/analytics/devices?startDate=${startDate}&endDate=${endDate}`);
      const json = await res.json();
      if (res.ok) {
        setData(json);
      } else {
        setError(json.details || json.error || 'Ошибка получения данных об устройствах');
      }
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
    }, [startDate, endDate]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const refetch = () => {
    fetchData();
  };

  return { data, loading, error, refetch };
};
