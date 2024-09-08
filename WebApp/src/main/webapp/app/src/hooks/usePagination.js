import { useState, useEffect } from 'react';

/**
 * Custom hook to handle pagination logic.
 *
 * @param {function} fetchData - The function to fetch data. It should return a promise resolving to an object with `data` and `totalAds`.
 * @param {number} [initialAdsPerPage=10] - Initial number of ads per page.
 *
 * @returns {object} Pagination state and handlers.
 * @returns {array} data - The current page data.
 * @returns {function} setData - Function to set the current page data.
 * @returns {boolean} loading - Loading state.
 * @returns {string|null} error - Error message, if any.
 * @returns {function} setError - Function to set the error message.
 * @returns {number} currentPage - The current page number.
 * @returns {function} setCurrentPage - Function to set the current page number.
 * @returns {number} adsPerPage - Number of ads per page.
 * @returns {number} totalAds - Total number of ads.
 * @returns {function} handleNextPage - Handler to go to the next page.
 * @returns {function} handlePreviousPage - Handler to go to the previous page.
 * @returns {function} handleAdsPerPageChange - Handler to change the number of ads per page.
 */
const usePagination = (fetchData, initialAdsPerPage = 10) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [adsPerPage, setAdsPerPage] = useState(initialAdsPerPage);
  const [totalAds, setTotalAds] = useState(0);

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const result = await fetchData(currentPage, adsPerPage);
        setData(result.data);
        setTotalAds(result.totalAds || 0);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [currentPage, adsPerPage, fetchData]);

  const handleNextPage = () => {
    if (currentPage < Math.ceil(totalAds / adsPerPage)) {
      setCurrentPage((prevPage) => prevPage + 1);
    }
  };

  const handlePreviousPage = () => {
    if (currentPage > 1) {
      setCurrentPage((prevPage) => prevPage - 1);
    }
  };

  const handleAdsPerPageChange = (event) => {
    setAdsPerPage(parseInt(event.target.value, 10));
    setCurrentPage(1);
  };

  return {
    data,
    setData,
    loading,
    error,
    setError,
    currentPage,
    setCurrentPage,
    adsPerPage,
    totalAds,
    handleNextPage,
    handlePreviousPage,
    handleAdsPerPageChange,
  };
};

export default usePagination;
