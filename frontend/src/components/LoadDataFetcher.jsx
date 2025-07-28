import React, { useState, useEffect } from 'react';
import LoadChart from './LoadChart';
import LoadComparisonChart from './LoadComparisonChart';

function LoadDataFetcher() {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [status, setStatus] = useState('');
  const [data, setData] = useState(null);
  const [zones, setZones] = useState([]);
  const [selectedZone, setSelectedZone] = useState('ALL');

  const fetchLoadData = async (zoneParam = 'ALL', start = startDate, end = endDate) => {
    if (!start || !end) {
      setStatus('Please select both start and end dates.');
      setData(null);
      setZones([]);
      return;
    }
    if (end < start) {
      setStatus('End date must not be before start date.');
      setData(null);
      setZones([]);
      return;
    }

    setStatus('Loading...');
    try {
      let url = `http://localhost:8080/api/load-data?start=${start}&end=${end}`;
      if (zoneParam !== 'ALL') url += `&zone=${zoneParam}`;
      const response = await fetch(url);
      if (!response.ok) throw new Error('Network response was not ok');
      const result = await response.json();
      setData(result);

      const uniqueZones = Array.from(new Set(result.map(d => d.zone))).sort();
      setZones(['ALL', ...uniqueZones]);

      setSelectedZone(zoneParam);
      setStatus('Load data fetched successfully!');
    } catch (error) {
      setStatus('Error fetching load data: ' + error.message);
      setData(null);
      setZones([]);
    }
  };

  useEffect(() => {
    fetchLoadData(selectedZone, startDate, endDate);
  }, [startDate, endDate, selectedZone]);

  return (
    <div className="load-data-fetcher">
      <div className="input-row">
        <label>
          Start Date:
          <input
            type="date"
            value={startDate}
            onChange={e => setStartDate(e.target.value)}
          />
        </label>

        <label>
          End Date:
          <input
            type="date"
            value={endDate}
            onChange={e => setEndDate(e.target.value)}
          />
        </label>

        {zones.length > 1 && (
          <label>
            Select Zone:
            <select
              value={selectedZone}
              onChange={e => setSelectedZone(e.target.value)}
            >
              {zones.map(zone => (
                <option key={zone} value={zone}>
                  {zone}
                </option>
              ))}
            </select>
          </label>
        )}
      </div>

      <p>{status}</p>

      {data && data.length > 0 && <LoadChart data={data} />}

      {startDate && endDate && (
        <LoadComparisonChart
          startDate={startDate}
          endDate={endDate}
          zone={selectedZone}
        />
      )}
    </div>
  );
}

export default LoadDataFetcher;
