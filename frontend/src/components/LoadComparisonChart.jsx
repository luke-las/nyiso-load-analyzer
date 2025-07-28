import React, { useState, useEffect } from 'react';
import {
  LineChart, Line, XAxis, YAxis, Tooltip, Legend, CartesianGrid, ResponsiveContainer
} from 'recharts';

const COLORS = [
  '#8884d8', '#82ca9d', '#ffc658', '#ff7300', '#00c49f',
  '#0088fe', '#a83279', '#aa46be', '#ff6e6e', '#a0d911'
];

function LoadComparisonChart({ startDate, endDate, zone }) {
  const [data, setData] = useState(null);
  const [zones, setZones] = useState([]);
  const [status, setStatus] = useState('');

  useEffect(() => {
    if (!startDate || !endDate) {
      setData(null);
      setZones([]);
      setStatus('Select start and end dates to see comparison.');
      return;
    }

    async function fetchData() {
      setStatus('Loading comparison data...');
      try {
        const zoneParam = zone && zone !== 'ALL' ? `&zone=${zone}` : '';
        const url = `http://localhost:8080/api/load-data/comparison?start=${startDate}&end=${endDate}${zoneParam}`;
        const res = await fetch(url);
        if (!res.ok) throw new Error(`Fetch failed: ${res.statusText}`);

        const json = await res.json();

        // Transform data for charting
        const { combined, zones: allZones } = transformComparisonData(json.currentYear, json.lastYear);

        setData(combined);
        setZones(allZones);
        setStatus('');
      } catch (error) {
        setData(null);
        setZones([]);
        setStatus('Error: ' + error.message);
      }
    }

    fetchData();
  }, [startDate, endDate, zone]);

  if (status) return <p>{status}</p>;
  if (!data || data.length === 0) return <p>No comparison data available for the selected range.</p>;

  return (
    <ResponsiveContainer width="100%" height={400}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="date" tick={{ fontSize: 12 }} />
        <YAxis />
        <Tooltip />
        <Legend />
        {zones.map((zoneName, idx) => (
          <React.Fragment key={zoneName}>
            <Line
              type="monotone"
              dataKey={`${zoneName}-current`}
              stroke={COLORS[idx % COLORS.length]}
              name={`${zoneName} Current Year`}
              dot={false}
            />
            <Line
              type="monotone"
              dataKey={`${zoneName}-last`}
              stroke={COLORS[(idx + 1) % COLORS.length]}
              strokeDasharray="5 5"
              name={`${zoneName} Last Year`}
              dot={false}
            />
          </React.Fragment>
        ))}
      </LineChart>
    </ResponsiveContainer>
  );
}

export default LoadComparisonChart;

function transformComparisonData(currentYear, lastYear) {
  const zones = new Set([
    ...currentYear.map(d => d.zone),
    ...lastYear.map(d => d.zone),
  ]);

  const getMonthDay = (dateString) => dateString.substring(5, 10);

  const allDates = new Set([
    ...currentYear.map(d => getMonthDay(d.day)),
    ...lastYear.map(d => getMonthDay(d.day)),
  ]);

  const zonesArray = Array.from(zones).sort();
  const datesArray = Array.from(allDates).sort();

  const indexData = (arr) => {
    const map = new Map();
    arr.forEach(d => {
      const key = `${d.zone}_${getMonthDay(d.day)}`;
      map.set(key, d.avgLoad);
    });
    return map;
  };

  const currentYearMap = indexData(currentYear);
  const lastYearMap = indexData(lastYear);

  const combined = datesArray.map(date => {
    const obj = { date };

    zonesArray.forEach(zone => {
      const currentKey = `${zone}_${date}`;
      const lastKey = `${zone}_${date}`;
      obj[`${zone}-current`] = currentYearMap.get(currentKey) ?? null;
      obj[`${zone}-last`] = lastYearMap.get(lastKey) ?? null;
    });

    return obj;
  });

  return { combined, zones: zonesArray };
}